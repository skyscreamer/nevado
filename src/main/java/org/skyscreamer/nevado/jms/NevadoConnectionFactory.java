package org.skyscreamer.nevado.jms;

import javax.jms.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/18/12
 * Time: 8:49 PM
 */
public class NevadoConnectionFactory implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory,
        Serializable
{
    private String _awsAccessKey;
    private String _awsSecretKey;
    private String _clientID;
    private Integer _jmsDeliveryMode;
    private Long _jmsTTL;
    private Integer _jmsPriority;

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

    private NevadoConnection createNevadoConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        NevadoConnection connection = new NevadoConnection(awsAccessKey, awsSecretKey);
        connection.setClientID(_clientID);
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

    public void setClientID(String _clientID) {
        this._clientID = _clientID;
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
