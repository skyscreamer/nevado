package org.skyscreamer.nevado.jms;

import javax.jms.*;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/18/12
 * Time: 8:49 PM
 */
public class NevadoConnectionFactory implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory {
    private String _awsAccessKey;
    private String _awsSecretKey;
    private Integer _jmsDeliveryMode;
    private Long _jmsTTL;
    private Integer _jmsPriority;

    public NevadoConnectionFactory() {}

    public NevadoConnectionFactory(String awsAccessKey, String awsSecretKey) {
        this._awsAccessKey = awsAccessKey;
        this._awsSecretKey = awsSecretKey;
    }

    public QueueConnection createQueueConnection() throws JMSException {
        return createNevadoConnection(_awsAccessKey, _awsSecretKey);
    }

    public QueueConnection createQueueConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        return createNevadoConnection(awsAccessKey, awsSecretKey);
    }

    public Connection createConnection() throws JMSException {
        return createNevadoConnection(_awsAccessKey, _awsSecretKey);
    }

    public Connection createConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        return createNevadoConnection(awsAccessKey, awsSecretKey);
    }

    public TopicConnection createTopicConnection() throws JMSException {
        return createNevadoConnection(_awsAccessKey, _awsSecretKey);
    }

    public TopicConnection createTopicConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        return createNevadoConnection(awsAccessKey, awsSecretKey);
    }

    private NevadoConnection createNevadoConnection(String awsAccessKey, String awsSecretKey) {
        NevadoConnection connection = new NevadoConnection();
        connection.setAwsAccessKey(awsAccessKey);
        connection.setAwsSecretKey(awsSecretKey);
        connection.setOverrideJMSDeliveryMode(_jmsDeliveryMode);
        connection.setOverrideJMSPriority(_jmsPriority);
        connection.setOverrideJMSTTL(_jmsTTL);
        return connection;
    }

    // Getters & Setters
    public void setAwsAccessKey(String awsAccessKey) {
        _awsAccessKey = awsAccessKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        _awsSecretKey = awsSecretKey;
    }

    public void setOverrideJMSDeliveryMode(Integer jmsDeliveryMode) {
        _jmsDeliveryMode = jmsDeliveryMode;
    }

    public void setOverrideJMSTTL(Long jmsTTL) {
        _jmsTTL = jmsTTL;
    }

    public void setOverrideJMSPriority(Integer jmsPriority) {
        _jmsPriority = jmsPriority;
    }
}
