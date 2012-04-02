package org.skyscreamer.nevado.jms;


import javax.jms.*;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/19/12
 * Time: 9:43 AM
 */
public class NevadoConnection implements Connection, QueueConnection, TopicConnection {
    private String _awsAccessKey;
    private String _awsSecretKey;
    private String _clientID;
    private Integer _jmsDeliveryMode;
    private Long _jmsTTL;
    private Integer _jmsPriority;

    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        NevadoSession nevadoSession = new NevadoSession(_awsAccessKey, _awsSecretKey, transacted, acknowledgeMode);
        nevadoSession.setOverrideJMSDeliveryMode(_jmsDeliveryMode);
        nevadoSession.setOverrideJMSTTL(_jmsTTL);
        nevadoSession.setOverrideJMSPriority(_jmsPriority);
        return nevadoSession;
    }

    public ConnectionConsumer createConnectionConsumer(Queue queue, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stop() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public TopicSession createTopicSession(boolean b, int i) throws JMSException {
        throw new UnsupportedOperationException("Topics are not yet supported"); // TODO
    }

    public ConnectionConsumer createConnectionConsumer(Topic topic, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        throw new UnsupportedOperationException("Topics are not yet supported"); // TODO
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        throw new UnsupportedOperationException("Topics are not yet supported"); // TODO
    }

    // Getters & Setters
    public void setAwsAccessKey(String awsAccessKey) {
        _awsAccessKey = awsAccessKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        _awsSecretKey = awsSecretKey;
    }

    public String getClientID() {
        return _clientID;
    }

    public void setClientID(String _clientID) {
        _clientID = _clientID;
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
}
