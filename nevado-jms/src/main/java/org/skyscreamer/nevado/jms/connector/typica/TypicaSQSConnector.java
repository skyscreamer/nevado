package org.skyscreamer.nevado.jms.connector.typica;

import com.xerox.amazonws.common.AWSError;
import com.xerox.amazonws.common.AWSException;
import com.xerox.amazonws.common.ListResult;
import com.xerox.amazonws.common.Result;
import com.xerox.amazonws.sns.NotificationService;
import com.xerox.amazonws.sns.SNSException;
import com.xerox.amazonws.sqs2.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.nevado.jms.connector.AbstractSQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSMessage;
import org.skyscreamer.nevado.jms.connector.SQSQueue;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.InvalidMessage;
import org.skyscreamer.nevado.jms.message.NevadoProperty;

import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.ResourceAllocationException;
import javax.net.ssl.SSLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Connector for SQS-only implementation of the Nevado JMS driver.
 *
 * TODO: Put the check interval and optional back-off strategy into the NevadoDestinations so they can be
 *       configured on a per-destination basis.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TypicaSQSConnector extends AbstractSQSConnector {
    protected final QueueService _queueService;
    protected final NotificationService _notficationService;
    private static final String AWS_ERROR_CODE_AUTHENTICATION = "InvalidClientTokenId";

    public TypicaSQSConnector(String awsAccessKey, String awsSecretKey) {
        super(200);
        _queueService = new QueueService(awsAccessKey, awsSecretKey);
        _notficationService = new NotificationService(awsAccessKey, awsSecretKey);
    }

    public TypicaSQSConnector(String awsAccessKey, String awsSecretKey, boolean isSecure) {
        super(200);
        _queueService = new QueueService(awsAccessKey, awsSecretKey, isSecure);
        _notficationService = new NotificationService(awsAccessKey, awsSecretKey, isSecure);
    }

    public TypicaSQSConnector(String awsAccessKey, String awsSecretKey, boolean isSecure, long receiveCheckIntervalMs) {
        super(receiveCheckIntervalMs);
        if (receiveCheckIntervalMs < 200)
        {
            _log.warn("Reducing the receiveCheckInterval will increase your AWS costs.  " +
                    "Amazon charges each time a check is made: http://aws.amazon.com/sqs/pricing/");
        }
        _queueService = new QueueService(awsAccessKey, awsAccessKey, isSecure);
        _notficationService = new NotificationService(awsAccessKey, awsSecretKey, isSecure);
    }

    /**
     * Tests the connection.
     */
    public void test() throws JMSException {
        try {
            _queueService.listMessageQueues(null);
            _notficationService.listTopics(null);
        } catch (AWSException e) {
            throw handleAWSException("Connection test failed", e);
        }
    }

    public NevadoTopic createTopic(String topicName) throws JMSException {
        NevadoTopic topic = new NevadoTopic(topicName);
        getTopicARN(topic);
        return topic;
    }

    public void deleteTopic(NevadoTopic topic) throws JMSException {
        try {
            _notficationService.deleteTopic(getTopicARN(topic));
        } catch (SNSException e) {
            throw handleAWSException("Unable to delete message topic '" + topic, e);
        }
    }

    @Override
    public Collection<NevadoTopic> listTopics() throws JMSException {
        Collection<NevadoTopic> topics;
        ListResult<String> results;
        try {
            results = _notficationService.listTopics(null);
        } catch (SNSException e) {
            throw handleAWSException("Unable to list topics", e);
        }
        topics = new HashSet<NevadoTopic>(results.getItems().size());
        for(String arn : results.getItems()) {
            topics.add(new NevadoTopic(arn));
        }
        return topics;
    }

    public Collection<NevadoQueue> listQueues(String temporaryQueuePrefix) throws JMSException {
        Collection<NevadoQueue> queues;
        List<MessageQueue> sqsQueues;
        try {
            sqsQueues = _queueService.listMessageQueues(temporaryQueuePrefix);
        } catch (SQSException e) {
            throw handleAWSException("Unable to list queues with prefix '" + temporaryQueuePrefix + "'", e);
        }
        queues = new HashSet<NevadoQueue>(sqsQueues.size());
        for(MessageQueue sqsQueue : sqsQueues) {
            URL sqsURL = sqsQueue.getUrl();
            queues.add(new NevadoQueue(sqsURL));
        }
        return queues;
    }

    public String subscribe(NevadoTopic topic, NevadoQueue topicEndpoint) throws JMSException {
        String subscriptionArn;
        try {
            SQSQueue queue = getSQSQueue(topicEndpoint);
            String sqsArn = queue.getQueueARN();
            String snsArn = getTopicARN(topic);
            queue.setPolicy(getPolicy(snsArn, sqsArn));
            Result<String> subscribeResult = _notficationService.subscribe(getTopicARN(topic), "sqs", sqsArn);
            subscriptionArn =  subscribeResult.getResult();
        } catch (AWSException e) {
            throw handleAWSException("Unable to subscripe to topic " + topic, e);
        }
        return subscriptionArn;
    }

    public void unsubscribe(NevadoTopic topic) throws JMSException {
        if (topic == null) {
            throw new NullPointerException();
        }
        if (topic.getSubscriptionArn() == null) {
            throw new IllegalArgumentException("Topic doesn't have a subscription");
        }
        try {
            _notficationService.unsubscribe(topic.getSubscriptionArn());
        } catch (SNSException e) {
            throw handleAWSException("Unable to subscribe topic " + topic + " with sub ARN "
                    + topic.getSubscriptionArn(), e);
        }
    }

    @Override
    public void unsubscribeDurableQueueFromTopic(NevadoQueue queue) {
        // TODO
    }

    protected NevadoMessage convertSqsMessage(NevadoDestination destination, SQSMessage sqsMessage, boolean readOnly)
            throws JMSException
    {
        // Get the message
        NevadoMessage message;
        String messageBody;
        if (destination instanceof NevadoQueue)
        {
            messageBody = sqsMessage.getMessageBody();
        }
        else
        {
            try {
                messageBody = new JSONObject(sqsMessage.getMessageBody()).getString("Message");
            } catch (JSONException e) {
                throw new JMSException("Unable to parse JSON from message body: " + sqsMessage.getMessageBody());
            }
        }
        try {
            message = deserializeMessage(messageBody);
        } catch (JMSException e) {
            message = new InvalidMessage(e);
        }

        // Set the JMS Message ID
        if (message.nevadoPropertyExists(NevadoProperty.DisableMessageID)
            && (Boolean)message.getNevadoProperty(NevadoProperty.DisableMessageID))
        {
            message.setJMSMessageID(null);
        }
        else if (message.getJMSMessageID() == null)
        {
            message.setJMSMessageID("ID:" + sqsMessage.getMessageId());

        }

        // Set the receipt handle and the destination
        message.setNevadoProperty(NevadoProperty.SQSReceiptHandle, sqsMessage.getReceiptHandle());
        message.setJMSDestination(destination);

        // Set if this is readonly (browsing)
        message.setReadOnly(readOnly);

        return message;
    }

    protected void sendSNSMessage(NevadoTopic topic, String serializedMessage) throws JMSException {
        String arn = getTopicARN(topic);
        try {
            _notficationService.publish(arn, serializedMessage, null);
        } catch (SNSException e) {
            throw handleAWSException("Unable to send message to topic: " + arn, e);
        }
    }

    protected TypicaSQSQueue getSQSQueue(NevadoDestination destination) throws JMSException
    {
        if (destination == null)
        {
            throw new JMSException("Destination is null");
        }

        if (destination.isDeleted())
        {
            throw new InvalidDestinationException("Destination " + destination + " has been deleted");
        }

        NevadoQueue queue = (destination instanceof NevadoQueue) ? (NevadoQueue)destination
                : ((NevadoTopic)destination).getTopicEndpoint();
        MessageQueue sqsQueue;
        try {
            if (queue.getQueueUrl() == null)
            {
                sqsQueue = _queueService.getOrCreateMessageQueue(queue.getName());
                queue.setQueueUrl(sqsQueue.getUrl().toString());
            }
            else
            {
                sqsQueue = _queueService.getOrCreateMessageQueue(queue.getQueueUrl());
            }
        } catch (SQSException e) {
            throw handleAWSException("Unable to get message queue '" + destination, e);
        }

        // We always base64-encode the message already
        sqsQueue.setEncoding(false);

        return new TypicaSQSQueue(this, sqsQueue);
    }

    protected String getTopicARN(NevadoTopic topic) throws JMSException {
        if (topic.getArn() == null)
        {
            Result<String> result;
            try {
                result = _notficationService.createTopic(topic.getTopicName());
            } catch (SNSException e) {
                throw handleAWSException("Unable to create/lookup topic: " + topic, e);
            }
            topic.setArn(result.getResult());
        }
        return topic.getArn();
    }

    protected JMSException handleAWSException(String message, AWSException e) {
        JMSException jmsException;
        String exMessage = message + ": " + e.getMessage();
        _log.error(exMessage, e);
        if (e.getCause() != null &&
                (UnknownHostException.class.equals(e.getCause().getClass())
                        || SSLException.class.equals((e.getCause().getClass()))))
        {
            jmsException = new ResourceAllocationException(exMessage);
        }
        else if (isSecurityException(e))
        {
            jmsException = new JMSSecurityException(exMessage);
        }
        else
        {
            jmsException = new JMSException(exMessage);
        }
        return jmsException;
    }

    private boolean isSecurityException(AWSException e) {
        boolean securityException = false;
        if (e.getErrors().size() > 0)
        {
            for(AWSError awsError : e.getErrors())
            {
                if (AWS_ERROR_CODE_AUTHENTICATION.equals(awsError.getCode()))
                {
                    securityException = true;
                    break;
                }
            }
        }
        return securityException;
    }
}
