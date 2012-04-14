package org.skyscreamer.nevado.jms;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.connector.NevadoConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.jms.Queue;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Nevado's implementation of JMS Connection.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoConnection implements Connection, QueueConnection, TopicConnection {
    public static final String TEMPORARY_QUEUE_PREFIX = "nevado_temp";

    private final Log _log = LogFactory.getLog(getClass());

    private boolean _inUse = false;
    private final NevadoConnector _nevadoConnector;
    private String _clientID;
    private Integer _jmsDeliveryMode;
    private Long _jmsTTL;
    private Integer _jmsPriority;
    private boolean _running = false;
    private volatile ExceptionListener _exceptionListener;
    private final List<NevadoSession> _sessions = new CopyOnWriteArrayList<NevadoSession>();
    private final Set<NevadoTemporaryQueue> _temporaryQueues = new HashSet<NevadoTemporaryQueue>();

    public NevadoConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        _nevadoConnector = new SQSConnector(awsAccessKey, awsSecretKey);
        _nevadoConnector.test();
    }

    public synchronized QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        _inUse = true;
        NevadoSession nevadoSession = new NevadoSession(this, transacted, acknowledgeMode);
        nevadoSession.setOverrideJMSDeliveryMode(_jmsDeliveryMode);
        nevadoSession.setOverrideJMSTTL(_jmsTTL);
        nevadoSession.setOverrideJMSPriority(_jmsPriority);
        _sessions.add(nevadoSession);
        if (_running)
        {
            nevadoSession.start();
        }
        return nevadoSession;
    }

    public ConnectionConsumer createConnectionConsumer(Queue queue, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        _inUse = true;
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        _inUse = true;
        return createQueueSession(transacted, acknowledgeMode);
    }

    public ConnectionMetaData getMetaData() throws JMSException {
        return NevadoConnectionMetaData.getInstance();
    }

    public ExceptionListener getExceptionListener() {
        return _exceptionListener;
    }

    public void setExceptionListener(ExceptionListener exceptionListener) {
        _exceptionListener = exceptionListener;
    }

    public synchronized void start() throws JMSException {
        _inUse = true;
        _running = true;
        for(NevadoSession session : _sessions)
        {
            session.start();
        }
    }

    public synchronized void stop() throws JMSException {
        _running = false;
        for(NevadoSession session : _sessions)
        {
            session.stop();
        }
    }

    public synchronized void close() throws JMSException {
        for(NevadoSession session : _sessions)
        {
            session.close();
        }
        for(NevadoTemporaryQueue temporaryQueue : new ArrayList<NevadoTemporaryQueue>(_temporaryQueues)) {
            try {
                deleteTemporaryQueue(temporaryQueue);
            } catch (JMSException e) {
                // Log but continue
                _log.error("Unable to delete temporaryQueue " + temporaryQueue, e);
            }
        }
        _temporaryQueues.clear();
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        _inUse = true;
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public TopicSession createTopicSession(boolean b, int i) throws JMSException {
        _inUse = true;
        throw new UnsupportedOperationException("Topics are not yet supported"); // TODO
    }

    public ConnectionConsumer createConnectionConsumer(Topic topic, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        _inUse = true;
        throw new UnsupportedOperationException("Topics are not yet supported"); // TODO
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        _inUse = true;
        throw new UnsupportedOperationException("Topics are not yet supported"); // TODO
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        String tempQueueName = TEMPORARY_QUEUE_PREFIX + UUID.randomUUID();
        NevadoQueue queue = getSQSConnector().createQueue(tempQueueName);
        NevadoTemporaryQueue temporaryQueue = new NevadoTemporaryQueue(this, queue);
        _temporaryQueues.add(temporaryQueue);
        return temporaryQueue;
    }

    public void deleteTemporaryQueue(NevadoTemporaryQueue temporaryQueue) throws JMSException {
        getSQSConnector().deleteQueue(temporaryQueue);
        _temporaryQueues.remove(temporaryQueue);
    }

    public Collection<TemporaryQueue> listAllTemporaryQueues() throws JMSException {
        Collection<NevadoQueue> queues = getSQSConnector().listQueues(TEMPORARY_QUEUE_PREFIX);
        Collection<TemporaryQueue> temporaryQueues = new HashSet<TemporaryQueue>(queues.size());
        for(NevadoQueue queue : queues) {
            temporaryQueues.add(new NevadoTemporaryQueue(this, queue));
        }
        return temporaryQueues;
    }

    // Getters & Setters
    protected NevadoConnector getSQSConnector() {
        return _nevadoConnector;
    }

    public String getClientID() {
        return _clientID;
    }

    public void setClientID(String clientID) throws IllegalStateException {
        if (_clientID != null) {
            throw new IllegalStateException("Client ID has already been set");
        }
        if (_inUse) {
            throw new IllegalStateException("Client ID cannot be set after the connection is in use");
        }
        _clientID = clientID;
    }

    public void setOverrideJMSDeliveryMode(Integer jmsDeliveryMode) {
        _jmsDeliveryMode = jmsDeliveryMode;
    }

    public void setOverrideJMSPriority(Integer jmsPriority) {
        _jmsPriority = jmsPriority;
    }

    public void setOverrideJMSTTL(Long jmsTTL) {
        _jmsTTL = jmsTTL;
    }

    public boolean isRunning() {
        return _running;
    }
}
