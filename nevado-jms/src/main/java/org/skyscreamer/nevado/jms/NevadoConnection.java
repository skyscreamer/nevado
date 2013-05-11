package org.skyscreamer.nevado.jms;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.destination.*;

import javax.jms.*;
import javax.jms.IllegalStateException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Nevado's implementation of JMS Connection.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoConnection implements Connection {
    private final Log _log = LogFactory.getLog(getClass());

    private final AtomicBoolean _closed = new AtomicBoolean(false);
    private final AtomicBoolean _running = new AtomicBoolean(false);

    protected volatile boolean _inUse = false;
    private final SQSConnector _sqsConnector;
    private volatile String _clientID;
    private volatile String _connectionID = UUID.randomUUID().toString();
    private volatile Integer _jmsDeliveryMode;
    private volatile Long _jmsTTL;
    private volatile Integer _jmsPriority;
    private volatile ExceptionListener _exceptionListener;
    private final List<NevadoSession> _sessions = new CopyOnWriteArrayList<NevadoSession>();
    private final Set<NevadoDestination> _temporaryDestinations = new CopyOnWriteArraySet<NevadoDestination>();

    public NevadoConnection(SQSConnector sqsConnector) throws JMSException {
        _sqsConnector = sqsConnector;
        _sqsConnector.test();
    }

    @Override
    public NevadoSession createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        _inUse = true;
        NevadoSession nevadoSession = new NevadoSession(this, transacted, acknowledgeMode);
        initializeSession(nevadoSession);
        return nevadoSession;
    }

    protected void initializeSession(NevadoSession nevadoSession) {
        nevadoSession.setOverrideJMSDeliveryMode(_jmsDeliveryMode);
        nevadoSession.setOverrideJMSTTL(_jmsTTL);
        nevadoSession.setOverrideJMSPriority(_jmsPriority);
        synchronized (_running) {
            _sessions.add(nevadoSession);
            if (_running.get())
            {
                nevadoSession.start();
            }
        }
    }

    @Override
    public NevadoConnectionMetaData getMetaData() throws JMSException {
        return NevadoConnectionMetaData.getInstance();
    }

    @Override
    public ExceptionListener getExceptionListener() {
        return _exceptionListener;
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener) throws IllegalStateException {
        checkClosed();
        _exceptionListener = exceptionListener;
    }

    @Override
    public void start() throws JMSException
    {
        checkClosed();
        _inUse = true;
        synchronized (_running) {
            _running.set(true);
            for(NevadoSession session : _sessions)
            {
                session.start();
            }
        }
    }

    @Override
    public void stop() throws JMSException
    {
        checkClosed();
        synchronized (_running) {
            _running.set(false);
            for(NevadoSession session : _sessions)
            {
                session.stop();
            }
        }
    }

    @Override
    public void close() throws JMSException {
        synchronized (_closed) {
            if (!_closed.get()) {
                stop();
                List<JMSException> sessionExceptions = new ArrayList<JMSException>();
                for(NevadoSession session : _sessions)
                {
                    try {
                        session.close();
                    } catch (JMSException e) {
                        sessionExceptions.add(e);
                        _log.warn("Caught exception closing a session.  Will continue trying to clean up, then will " +
                                "throw it up the stack.  (First one if multiple.)", e);
                    }
                }
                for(NevadoDestination temporaryDestination : new ArrayList<NevadoDestination>(_temporaryDestinations)) {
                    try {
                        if (temporaryDestination instanceof NevadoTemporaryQueue)
                        {
                            deleteTemporaryQueue((NevadoTemporaryQueue)temporaryDestination);
                        }
                        else if (temporaryDestination instanceof NevadoTemporaryTopic)
                        {
                            deleteTemporaryTopic((NevadoTemporaryTopic) temporaryDestination);
                        }
                        else
                        {
                            throw new IllegalStateException("Unexpected temporary destination of type: "
                                    + temporaryDestination.getClass().getName());
                        }

                    } catch (JMSException e) {
                        // Log but continue
                        _log.error("Unable to delete temporary destination " + temporaryDestination, e);
                    }
                }
                _temporaryDestinations.clear();
                _closed.set(true);
                if (sessionExceptions.size() > 0) {
                    throw sessionExceptions.get(0);
                }
            }
        }
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        checkClosed();
        _inUse = true;
        return null;  // TODO
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        checkClosed();
        _inUse = true;
        return null;  // TODO
    }

    protected NevadoTemporaryTopic createTemporaryTopic() throws JMSException {
        checkClosed();
        String tempTopicName = "" + NevadoProviderQueuePrefix.TEMPORARY_DESTINATION_PREFIX + UUID.randomUUID();
        NevadoTopic topic = getSQSConnector().createTopic(tempTopicName);
        NevadoTemporaryTopic temporaryTopic = new NevadoTemporaryTopic(this, topic);
        _temporaryDestinations.add(temporaryTopic);
        return temporaryTopic;
    }

    public void deleteTemporaryTopic(NevadoTemporaryTopic temporaryTopic) throws JMSException
    {
        checkClosed();
        deleteTopic(temporaryTopic);
        _temporaryDestinations.remove(temporaryTopic);
    }

    public void deleteTopic(NevadoTopic topic) throws JMSException {
        getSQSConnector().deleteTopic(topic);
        topic.setDeleted(true);
    }

    protected NevadoTemporaryQueue createTemporaryQueue() throws JMSException
    {
        checkClosed();
        String tempQueueName = "" + NevadoProviderQueuePrefix.TEMPORARY_DESTINATION_PREFIX + UUID.randomUUID();
        NevadoQueue queue = getSQSConnector().createQueue(tempQueueName);
        NevadoTemporaryQueue temporaryQueue = new NevadoTemporaryQueue(this, queue);
        _temporaryDestinations.add(temporaryQueue);
        return temporaryQueue;
    }

    public void deleteTemporaryQueue(NevadoTemporaryQueue temporaryQueue) throws JMSException
    {
        checkClosed();
        deleteQueue(temporaryQueue);
        _temporaryDestinations.remove(temporaryQueue);
    }

    protected void deleteQueue(NevadoQueue queue) throws JMSException {
        _sqsConnector.deleteQueue(queue);
        queue.setDeleted(true);
    }

    protected boolean ownsTemporaryDestination(Destination temporaryDestination)
    {
        return _temporaryDestinations.contains(temporaryDestination);
    }

    public Collection<TemporaryQueue> listAllTemporaryQueues() throws JMSException {
        Collection<NevadoQueue> queues = getSQSConnector()
                .listQueues(NevadoProviderQueuePrefix.TEMPORARY_DESTINATION_PREFIX + "");
        Collection<TemporaryQueue> temporaryQueues = new HashSet<TemporaryQueue>(queues.size());
        for(NevadoQueue queue : queues) {
            temporaryQueues.add(new NevadoTemporaryQueue(this, queue));
        }
        return temporaryQueues;
    }

    public Collection<TemporaryTopic> listAllTemporaryTopics() throws JMSException {
        Collection<NevadoTopic> topics = getSQSConnector().listTopics();
        Collection<TemporaryTopic> temporaryTopics = new HashSet<TemporaryTopic>(topics.size());
        for(NevadoTopic topic : topics) {
            if (topic.getTopicName().startsWith(NevadoProviderQueuePrefix.TEMPORARY_DESTINATION_PREFIX + ""))
                temporaryTopics.add(new NevadoTemporaryTopic(this, topic));
        }
        return temporaryTopics;
    }

    public String subscribe(NevadoTopic topic, NevadoQueue topicEndpoint) throws JMSException {
        return getSQSConnector().subscribe(topic, topicEndpoint);
    }

    public void unsubscribe(NevadoTopic topic) throws JMSException {
        getSQSConnector().unsubscribe(topic);
    }

    // Getters & Setters
    public SQSConnector getSQSConnector() {
        return _sqsConnector;
    }

    @Override
    public String getClientID() {
        return _clientID;
    }

    @Override
    public void setClientID(String clientID) throws JMSException {
        checkClosed();
        if (clientID == null || clientID.trim().length() == 0)
        {
            throw new InvalidClientIDException("Client ID is empty");
        }
        if (_clientID != null) {
            throw new IllegalStateException("Client ID has already been set");
        }
        if (_inUse) {
            throw new IllegalStateException("Client ID cannot be set after the connection is in use");
        }
        if (clientID != null && !clientID.matches("^[\\w\\-_]+$"))
        {
            throw new InvalidClientIDException("Client ID can only include alphanumeric characters, hyphens, or underscores");
        }
        _clientID = clientID;
        _connectionID = clientID;
    }

    public void setOverrideJMSDeliveryMode(Integer jmsDeliveryMode) throws IllegalStateException {
        checkClosed();
        _jmsDeliveryMode = jmsDeliveryMode;
    }

    public void setOverrideJMSPriority(Integer jmsPriority) throws IllegalStateException {
        checkClosed();
        _jmsPriority = jmsPriority;
    }

    public void setOverrideJMSTTL(Long jmsTTL) throws IllegalStateException {
        checkClosed();
        _jmsTTL = jmsTTL;
    }

    public boolean isRunning() {
        return _running.get();
    }

    /**
     * Tell whether the connection is closed.  If the connection is in the process of closing, this
     * will return true until complete.
     *
     * @return true if closed, otherwise false
     */
    public boolean isClosed() {
        return _closed.get();
    }

    protected void checkClosed() throws IllegalStateException {
        if (_closed.get())
        {
            throw new IllegalStateException("Connection is closed");
        }
    }

    public String getConnectionID() {
        return _connectionID;
    }
}
