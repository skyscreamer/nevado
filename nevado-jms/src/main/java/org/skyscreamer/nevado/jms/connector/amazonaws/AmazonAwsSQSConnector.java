package org.skyscreamer.nevado.jms.connector.amazonaws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import org.skyscreamer.nevado.jms.connector.AbstractSQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSMessage;
import org.skyscreamer.nevado.jms.connector.SQSQueue;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.JMSXProperty;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.ResourceAllocationException;
import javax.net.ssl.SSLException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Connector for SQS-only implementation of the Nevado JMS driver.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class AmazonAwsSQSConnector extends AbstractSQSConnector {
    public static final String MESSAGE_ATTRIBUTE_APPROXIMATE_RECEIVE_COUNT = "ApproximateReceiveCount";

    private final AmazonSQS _amazonSQS;
    private final AmazonSNS _amazonSNS;

    private boolean _testAlwaysPasses = false;


    public AmazonAwsSQSConnector(String awsAccessKey, String awsSecretKey, boolean isSecure, long receiveCheckIntervalMs) {
        this(awsAccessKey, awsSecretKey, isSecure, receiveCheckIntervalMs, false);
    }

    public AmazonAwsSQSConnector(String awsAccessKey, String awsSecretKey, boolean isSecure, long receiveCheckIntervalMs, boolean isAsync) {
        super(receiveCheckIntervalMs, isAsync);
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if(proxyHost != null){
            clientConfiguration.setProxyHost(proxyHost);
            if(proxyPort != null){
              clientConfiguration.setProxyPort(Integer.parseInt(proxyPort));
            }
        }
        clientConfiguration.setProtocol(isSecure ? Protocol.HTTPS : Protocol.HTTP);
        if (isAsync) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            _amazonSQS = new AmazonSQSAsyncClient(awsCredentials, clientConfiguration, executorService);
            _amazonSNS = new AmazonSNSAsyncClient(awsCredentials, clientConfiguration, executorService);
        } else {
            _amazonSQS = new AmazonSQSClient(awsCredentials, clientConfiguration);
            _amazonSNS = new AmazonSNSClient(awsCredentials, clientConfiguration);
        }
    }

    public boolean isTestAlwaysPasses() {
        return _testAlwaysPasses;
    }

    public void setTestAlwaysPasses(boolean _testAlwaysPasses) {
        this._testAlwaysPasses = _testAlwaysPasses;
    }

    @Override
    protected void sendSNSMessage(NevadoTopic topic, String serializedMessage) throws JMSException {
        String arn = getTopicARN(topic);
        PublishRequest request = new PublishRequest(arn, serializedMessage);
        try {
            if (isAsync()) {
                ((AmazonSNSAsync)_amazonSNS).publishAsync(request);
            } else {
                _amazonSNS.publish(request);
            }
        }
        catch (AmazonClientException e) {
            throw handleAWSException("Unable to send message to topic: " + arn, e);
        }
    }

    @Override
    protected AmazonAwsSQSQueue getSQSQueueImpl(NevadoQueue queue) throws JMSException {
        try {
            if (queue.getQueueUrl() == null)
            {
                CreateQueueResult result = _amazonSQS.createQueue(new CreateQueueRequest(queue.getQueueName()));
                queue.setQueueUrl(result.getQueueUrl());
            }
        } catch (AmazonClientException e) {
            throw handleAWSException("Unable to get message queue '" + queue, e);
        }

        return new AmazonAwsSQSQueue(this, queue.getQueueUrl(), isAsync());
    }

    @Override
    public void test() throws JMSException {
        if (isTestAlwaysPasses()) {
            return;
        }
        try {
            _amazonSQS.listQueues();
            _amazonSNS.listTopics();
        } catch (AmazonClientException e) {
            throw handleAWSException("Connection test failed", e);
        }
    }

    @Override
    public Collection<NevadoQueue> listQueues(String temporaryQueuePrefix) throws JMSException {
        Collection<NevadoQueue> queues;
        ListQueuesResult result;
        try {
            result = _amazonSQS.listQueues(new ListQueuesRequest().withQueueNamePrefix(temporaryQueuePrefix));
        } catch (AmazonClientException e) {
            throw handleAWSException("Unable to list queues with prefix '" + temporaryQueuePrefix + "'", e);
        }
        queues = new HashSet<NevadoQueue>(result.getQueueUrls().size());
        for(String queueUrlString : result.getQueueUrls()) {
            URL queueURL = null;
            try {
                queueURL = new URL(queueUrlString);
            } catch (MalformedURLException e) {
                throw new JMSException("Unable to parse URL for: " + queueUrlString);
            }
            queues.add(new NevadoQueue(queueURL));
        }
        return queues;
    }

    @Override
    public NevadoTopic createTopic(String topicName) throws JMSException {
        NevadoTopic topic = new NevadoTopic(topicName);
        getTopicARN(topic);
        return topic;
    }

    @Override
    public void deleteTopic(NevadoTopic topic) throws JMSException {
        try {
            _amazonSNS.deleteTopic(new DeleteTopicRequest().withTopicArn(getTopicARN(topic)));
        } catch (AmazonClientException e) {
            throw handleAWSException("Unable to delete message topic '" + topic, e);
        }
    }

    @Override
    public Collection<NevadoTopic> listTopics() throws JMSException {
        Collection<NevadoTopic> topics;
        ListTopicsResult result;
        try {
            result = _amazonSNS.listTopics();
        } catch (AmazonClientException e) {
            throw handleAWSException("Unable to list topics", e);
        }
        topics = new HashSet<NevadoTopic>(result.getTopics().size());
        for(Topic topic : result.getTopics()) {
            topics.add(new NevadoTopic(topic.getTopicArn()));
        }
        return topics;
    }

    @Override
    public String subscribe(NevadoTopic topic, NevadoQueue topicEndpoint) throws JMSException {
        String subscriptionArn;
        try {
            SQSQueue queue = getSQSQueue((NevadoDestination) topicEndpoint);
            String sqsArn = queue.getQueueARN();
            String snsArn = getTopicARN(topic);
            queue.setPolicy(getPolicy(snsArn, sqsArn));
            subscriptionArn = _amazonSNS.subscribe(new SubscribeRequest().withTopicArn(getTopicARN(topic))
                    .withProtocol("sqs").withEndpoint(sqsArn)).getSubscriptionArn();
        } catch (AmazonClientException e) {
            throw handleAWSException("Unable to subscripe to topic " + topic, e);
        }
        return subscriptionArn;
    }

    @Override
    public void unsubscribe(NevadoTopic topic) throws JMSException {
        if (topic == null) {
            throw new NullPointerException();
        }
        if (topic.getSubscriptionArn() == null) {
            throw new IllegalArgumentException("Topic doesn't have a subscription");
        }
        try {
            _amazonSNS.unsubscribe(new UnsubscribeRequest().withSubscriptionArn(topic.getSubscriptionArn()));
        } catch (AmazonClientException e) {
            throw handleAWSException("Unable to subscribe topic " + topic + " with sub ARN "
                    + topic.getSubscriptionArn(), e);
        }
    }

    @Override
    protected NevadoMessage convertSqsMessage(NevadoDestination destination, SQSMessage sqsMessage, boolean readOnly) throws JMSException {
        NevadoMessage message = super.convertSqsMessage(destination, sqsMessage, false);
        if (sqsMessage.getAttributes() != null) {
            Integer count = null;
            String countAttr = sqsMessage.getAttributes().get(MESSAGE_ATTRIBUTE_APPROXIMATE_RECEIVE_COUNT);
            if (countAttr != null) {
                try {
                    count = Integer.parseInt(countAttr);
                } catch (NumberFormatException e) {
                    _log.warn("Unable to parse " + MESSAGE_ATTRIBUTE_APPROXIMATE_RECEIVE_COUNT + ": " + countAttr);
                }
            }

            if (count != null) {
                message.setObjectPropertyIgnoreReadOnly(JMSXProperty.JMSXDeliveryCount + "", count);
            }
        }
        message.setReadOnly(readOnly);
        return message;
    }

    public AmazonSQS getAmazonSQS() {
        return _amazonSQS;
    }

    public AmazonSNS getAmazonSNS() {
        return _amazonSNS;
    }

    protected String getTopicARN(NevadoTopic topic) throws JMSException {
        if (topic.getArn() == null)
        {
            CreateTopicResult result;
            try {
                result = _amazonSNS.createTopic(new CreateTopicRequest(topic.getTopicName()));
            }
            catch (AmazonClientException e) {
                throw handleAWSException("Unable to create/lookup topic: " + topic, e);
            }
            topic.setArn(result.getTopicArn());
        }
        return topic.getArn();
    }

    protected JMSException handleAWSException(String message, AmazonClientException e) {
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

    private boolean isSecurityException(AmazonClientException e) {
        if (e instanceof AmazonServiceException) {
            return AWS_ERROR_CODE_AUTHENTICATION.equals(((AmazonServiceException)e).getErrorCode());
        }
        else {
            return false;
        }
    }
}
