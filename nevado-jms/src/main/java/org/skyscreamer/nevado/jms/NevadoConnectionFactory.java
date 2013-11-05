package org.skyscreamer.nevado.jms;

import org.apache.commons.lang.StringUtils;
import org.skyscreamer.nevado.jms.connector.CloudCredentials;
import org.skyscreamer.nevado.jms.connector.SQSConnectorFactory;
import org.skyscreamer.nevado.jms.resource.NevadoReferencableFactory;
import org.skyscreamer.nevado.jms.util.SerializeUtil;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.naming.*;
import java.io.IOException;
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
    public static final String JNDI_CLOUD_CREDENTIALS = "cloudCredentials";
    public static final String JNDI_CLIENT_ID = "clientID";
    public static final String JNDI_JMS_DELIVERY_MODE = "jmsDeliveryMode";
    public static final String JNDI_JMS_TTL = "jmsTTL";
    public static final String JNDI_JMS_PRIORITY = "jmsPriority";

    private SQSConnectorFactory _sqsConnectorFactory;
    private CloudCredentials _cloudCredentials;
    private volatile String _clientID;
    private volatile Integer _jmsDeliveryMode;
    private volatile Long _jmsTTL;
    private volatile Integer _jmsPriority;
    private String _temporaryQueueSuffix = "";
    private String _temporaryTopicSuffix = "";
    private long _maxPollWaitMs = NevadoConnection.DEFAULT_MAX_POLL_WAIT_MS;

    public NevadoConnectionFactory() {}

    public NevadoConnectionFactory(SQSConnectorFactory sqsConnectorFactory) {
        _sqsConnectorFactory = sqsConnectorFactory;
    }

    public NevadoQueueConnection createQueueConnection() throws JMSException {
        checkSQSConnectorFactory();
        NevadoQueueConnection connection = new NevadoQueueConnection(_sqsConnectorFactory.getInstance(_cloudCredentials));
        initializeConnection(connection);
        return connection;
    }

    public NevadoQueueConnection createQueueConnection(String s1, String s2) throws JMSException {
        throw new UnsupportedOperationException("Credentials must be set directly in NevadoConnectionFactory.");
    }

    public NevadoConnection createConnection() throws JMSException {
        checkSQSConnectorFactory();
        NevadoConnection connection = new NevadoConnection(_sqsConnectorFactory.getInstance(_cloudCredentials));
        initializeConnection(connection);
        return connection;
    }

    public NevadoConnection createConnection(String s1, String s2) throws JMSException {
        throw new UnsupportedOperationException("Credentials must be set directly in NevadoConnectionFactory.");
    }

    public NevadoTopicConnection createTopicConnection() throws JMSException {
        checkSQSConnectorFactory();
        NevadoTopicConnection connection = new NevadoTopicConnection(_sqsConnectorFactory.getInstance(_cloudCredentials));
        initializeConnection(connection);
        return connection;
    }

    public TopicConnection createTopicConnection(String s1, String s2) throws JMSException {
        throw new UnsupportedOperationException("Credentials must be set directly in NevadoConnectionFactory.");
    }

    private void initializeConnection(NevadoConnection connection) throws JMSException {
        if (StringUtils.isNotEmpty(_clientID))
        {
            connection.setClientID(_clientID);
        }
        connection.setOverrideJMSDeliveryMode(_jmsDeliveryMode);
        connection.setOverrideJMSPriority(_jmsPriority);
        connection.setOverrideJMSTTL(_jmsTTL);
        connection.setTemporaryQueueSuffix(_temporaryQueueSuffix);
        connection.setTemporaryTopicSuffix(_temporaryTopicSuffix);
        connection.setMaxPollWaitMs(_maxPollWaitMs);
    }

    // Getters & Setters
    public void setSqsConnectorFactory(SQSConnectorFactory sqsConnectorFactory) {
        _sqsConnectorFactory = sqsConnectorFactory;
    }

    public void setCloudCredentials(CloudCredentials cloudCredentials) {
        _cloudCredentials = cloudCredentials;
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

    public CloudCredentials getCloudCredentials() {
        return _cloudCredentials;
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

    public void setTemporaryQueueSuffix(String temporaryQueueSuffix) {
        _temporaryQueueSuffix = temporaryQueueSuffix;
    }

    public void setTemporaryTopicSuffix(String temporaryTopicSuffix) {
        _temporaryTopicSuffix = temporaryTopicSuffix;
    }

    public void setMaxPollWaitMs(long maxPollWaitMs) {
        _maxPollWaitMs = maxPollWaitMs;
    }

    public Reference getReference() throws NamingException {
        Reference reference = null;
        try {
            reference = new Reference(NevadoConnectionFactory.class.getName(),
                    new BinaryRefAddr(JNDI_CLOUD_CREDENTIALS, SerializeUtil.serialize(_cloudCredentials)),
                    NevadoReferencableFactory.class.getName(), null);
        } catch (IOException e) {
            throw new NamingException("Unable to serialize cloud credentials: " + e.getMessage());
        }
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

