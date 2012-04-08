package org.skyscreamer.nevado.jms;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Nevado's implementation of JMS Connection.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoConnection implements Connection, QueueConnection, TopicConnection {
    private final Log _log = LogFactory.getLog(getClass());

    private boolean _inUse = false;
    private final SQSConnector _sqsConnector;
    private String _clientID;
    private Integer _jmsDeliveryMode;
    private Long _jmsTTL;
    private Integer _jmsPriority;
    private boolean _running = false;

    public NevadoConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        _sqsConnector = new SQSConnector(awsAccessKey, awsSecretKey);
        _sqsConnector.test();
    }

    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        _inUse = true;
        NevadoSession nevadoSession = new NevadoSession(this, transacted, acknowledgeMode);
        nevadoSession.setOverrideJMSDeliveryMode(_jmsDeliveryMode);
        nevadoSession.setOverrideJMSTTL(_jmsTTL);
        nevadoSession.setOverrideJMSPriority(_jmsPriority);
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

    public ExceptionListener getExceptionListener() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void start() throws JMSException {
        _inUse = true;
        _running = true;
    }

    public void stop() throws JMSException {
        _running = false;
    }

    public void close() throws JMSException {
        _running = false;
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

    // Getters & Setters
    protected SQSConnector getSQSConnector() {
        return _sqsConnector;
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
