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
        return connection;
    }

    // Getters & Setters
    public void setAwsAccessKey(String _awsAccessKey) {
        this._awsAccessKey = _awsAccessKey;
    }

    public void setAwsSecretKey(String _awsSecretKey) {
        this._awsSecretKey = _awsSecretKey;
    }
}
