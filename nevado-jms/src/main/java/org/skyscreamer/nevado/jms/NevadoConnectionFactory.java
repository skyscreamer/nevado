package org.skyscreamer.nevado.jms;

import org.apache.commons.lang.StringUtils;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnectorFactory;
import org.skyscreamer.nevado.jms.connector.mock.MockSQSConnector;
import org.skyscreamer.nevado.jms.connector.typica.TypicaSQSConnector;
import org.skyscreamer.nevado.jms.resource.NevadoReferencableFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/18/12
 * Time: 8:49 PM
 */
public class NevadoConnectionFactory implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory,
        Serializable, Referenceable
{
    public static final String JNDI_AWS_ACCESS_KEY = "awsAccessKey";
    public static final String JNDI_AWS_SECRET_KEY = "awsSecretKey";
    public static final String JNDI_CLIENT_ID = "clientID";
    public static final String JNDI_JMS_DELIVERY_MODE = "jmsDeliveryMode";
    public static final String JNDI_JMS_TTL = "jmsTTL";
    public static final String JNDI_JMS_PRIORITY = "jmsPriority";

    private SQSConnectorFactory _sqsConnectorFactory;
    private volatile String _awsAccessKey;
    private volatile String _awsSecretKey;
    private volatile String _clientID;
    private volatile Integer _jmsDeliveryMode;
    private volatile Long _jmsTTL;
    private volatile Integer _jmsPriority;

    public NevadoConnectionFactory() {}

    public NevadoConnectionFactory(SQSConnectorFactory sqsConnectorFactory) {
        _sqsConnectorFactory = sqsConnectorFactory;
    }

    public NevadoQueueConnection createQueueConnection() throws JMSException {
        checkSQSConnectorFactory();
        NevadoQueueConnection connection = new NevadoQueueConnection(_sqsConnectorFactory.getInstance(_awsAccessKey, _awsSecretKey));
        initializeConnection(connection);
        return connection;
    }

    public NevadoQueueConnection createQueueConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        checkSQSConnectorFactory();
        NevadoQueueConnection connection
                = new NevadoQueueConnection(_sqsConnectorFactory.getInstance(awsAccessKey, awsSecretKey));
        initializeConnection(connection);
        return connection;
    }

    public NevadoConnection createConnection() throws JMSException {
        checkSQSConnectorFactory();
        NevadoConnection connection = new NevadoConnection(_sqsConnectorFactory.getInstance(_awsAccessKey, _awsSecretKey));
        initializeConnection(connection);
        return connection;
    }

    public NevadoConnection createConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        checkSQSConnectorFactory();
        NevadoConnection connection = new NevadoConnection(_sqsConnectorFactory.getInstance(awsAccessKey, awsSecretKey));
        initializeConnection(connection);
        return connection;
    }

    public NevadoTopicConnection createTopicConnection() throws JMSException {
        checkSQSConnectorFactory();
        NevadoTopicConnection connection = new NevadoTopicConnection(_sqsConnectorFactory.getInstance(_awsAccessKey, _awsSecretKey));
        initializeConnection(connection);
        return connection;
    }

    public TopicConnection createTopicConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        checkSQSConnectorFactory();
        NevadoTopicConnection connection = new NevadoTopicConnection(_sqsConnectorFactory.getInstance(awsAccessKey, awsSecretKey));
        initializeConnection(connection);
        return connection;
    }

    private void initializeConnection(NevadoConnection connection) throws JMSException {
        if (StringUtils.isNotEmpty(_clientID))
        {
            connection.setClientID(_clientID);
        }
        connection.setOverrideJMSDeliveryMode(_jmsDeliveryMode);
        connection.setOverrideJMSPriority(_jmsPriority);
        connection.setOverrideJMSTTL(_jmsTTL);
    }

    // Getters & Setters
    @Required
    public void setSqsConnectorFactory(SQSConnectorFactory sqsConnectorFactory) {
        _sqsConnectorFactory = sqsConnectorFactory;
    }

    public void setAwsAccessKey(String awsAccessKey) {
        _awsAccessKey = awsAccessKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        _awsSecretKey = awsSecretKey;
    }

    public void setClientID(String clientID) {
        _clientID = clientID;
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

    public String getAwsAccessKey() {
        return _awsAccessKey;
    }

    public String getAwsSecretKey() {
        return _awsSecretKey;
    }

    public String getClientID() {
        return _clientID;
    }

    public Integer getJMSDeliveryMode() {
        return _jmsDeliveryMode;
    }

    public Long getJMSTTL() {
        return _jmsTTL;
    }

    public Integer getJMSPriority() {
        return _jmsPriority;
    }

    public Reference getReference() throws NamingException {
        Reference reference = new Reference(NevadoConnectionFactory.class.getName(),
                new StringRefAddr(JNDI_AWS_ACCESS_KEY, _awsAccessKey),
                NevadoReferencableFactory.class.getName(), null);
        reference.add(new StringRefAddr(JNDI_AWS_SECRET_KEY, _awsSecretKey));
        if (_clientID != null)
        {
            reference.add(new StringRefAddr(JNDI_CLIENT_ID, _clientID));
        }
        if (_jmsDeliveryMode != null)
        {
            reference.add(new StringRefAddr(JNDI_JMS_DELIVERY_MODE, _jmsDeliveryMode.toString()));
        }
        if (_jmsTTL != null)
        {
            reference.add(new StringRefAddr(JNDI_JMS_TTL, _jmsTTL.toString()));
        }
        if (_jmsPriority != null)
        {
            reference.add(new StringRefAddr(JNDI_JMS_PRIORITY, _jmsPriority.toString()));
        }
        return reference;
    }

    private void checkSQSConnectorFactory() throws IllegalStateException {
        if (_sqsConnectorFactory == null)
        {
            throw new IllegalStateException("SQSConnectorFactory is null, it must be set.");
        }
    }
}

