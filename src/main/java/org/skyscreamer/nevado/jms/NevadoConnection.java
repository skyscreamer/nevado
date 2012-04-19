package org.skyscreamer.nevado.jms;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.connector.NevadoConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.destination.*;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.jms.Queue;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Nevado's implementation of JMS Connection.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoConnection implements Connection {
    private final Log _log = LogFactory.getLog(getClass());

    private boolean _closed = false;
    protected boolean _inUse = false;
    private final NevadoConnector _nevadoConnector;
    private String _clientID;
    private Integer _jmsDeliveryMode;
    private Long _jmsTTL;
    private Integer _jmsPriority;
    private boolean _running = false;
    private volatile ExceptionListener _exceptionListener;
    private final List<NevadoSession> _sessions = new CopyOnWriteArrayList<NevadoSession>();
    private final Set<NevadoDestination> _temporaryDestinations = new CopyOnWriteArraySet<NevadoDestination>();

    public NevadoConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        _nevadoConnector = new SQSConnector(awsAccessKey, awsSecretKey);
        _nevadoConnector.test();
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
        _sessions.add(nevadoSession);
        if (_running)
        {
            nevadoSession.start();
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
    public synchronized void start() throws JMSException
    {
        checkClosed();
        _inUse = true;
        _running = true;
        for(NevadoSession session : _sessions)
        {
            session.start();
        }
    }

    @Override
    public synchronized void stop() throws JMSException
    {
        checkClosed();
        _running = false;
        for(NevadoSession session : _sessions)
        {
            session.stop();
        }
    }

    @Override
    public synchronized void close() throws JMSException {
        if (!_closed) {
            stop();
            for(NevadoSession session : _sessions)
            {
                session.close();
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
            _closed = true;
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
        getSQSConnector().deleteTopic(temporaryTopic);
        _temporaryDestinations.remove(temporaryTopic);
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
        getSQSConnector().deleteQueue(temporaryQueue);
        _temporaryDestinations.remove(temporaryQueue);
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
    public NevadoConnector getSQSConnector() {
        return _nevadoConnector;
    }

    @Override
    public String getClientID() {
        return _clientID;
    }

    @Override
    public void setClientID(String clientID) throws IllegalStateException {
        checkClosed();
        if (_clientID != null) {
            throw new IllegalStateException("Client ID has already been set");
        }
        if (_inUse) {
            throw new IllegalStateException("Client ID cannot be set after the connection is in use");
        }
        _clientID = clientID;
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
        return _running;
    }

    public boolean isClosed() {
        return _closed;
    }

    protected void checkClosed() throws IllegalStateException {
        if (_closed)
        {
            throw new IllegalStateException("Connection is closed");
        }
    }
}
