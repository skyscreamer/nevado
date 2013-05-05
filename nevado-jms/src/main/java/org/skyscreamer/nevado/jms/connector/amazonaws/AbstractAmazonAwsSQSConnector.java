package org.skyscreamer.nevado.jms.connector.amazonaws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.model.UnsubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.ResourceAllocationException;
import javax.net.ssl.SSLException;
import org.skyscreamer.nevado.jms.connector.AbstractSQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSQueue;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;

/**
 * @author Siarhei Hlasouski
 *
 */
public abstract class AbstractAmazonAwsSQSConnector extends AbstractSQSConnector {

	protected AbstractAmazonAwsSQSConnector(long receiveCheckIntervalMs) {
		super(receiveCheckIntervalMs);
	}

	public abstract AmazonSQS getAmazonSQS();

	public abstract AmazonSNS getAmazonSNS();
	
	@Override
	protected void sendSNSMessage(NevadoTopic topic, String serializedMessage) throws JMSException {
		String arn = getTopicARN(topic);
		try {
			getAmazonSNS().publish(new PublishRequest(arn, serializedMessage));
		} catch (AmazonClientException e) {
			throw handleAWSException("Unable to send message to topic: " + arn, e);
		}
	}

	@Override
	protected AmazonAwsSQSQueue getSQSQueueImpl(NevadoQueue queue) throws JMSException {
		try {
			if (queue.getQueueUrl() == null) {
				CreateQueueResult result = getAmazonSQS().createQueue(new CreateQueueRequest(queue.getQueueName()));
				queue.setQueueUrl(result.getQueueUrl());
			}
		} catch (AmazonClientException e) {
			throw handleAWSException("Unable to get message queue '" + queue, e);
		}

		return new AmazonAwsSQSQueue(this, queue.getQueueUrl());
	}

	@Override
	public void test() throws JMSException {
		try {
			getAmazonSQS().listQueues();
			getAmazonSNS().listSubscriptions();
		} catch (AmazonClientException e) {
			throw handleAWSException("Connection test failed", e);
		}
	}

	@Override
	public Collection<NevadoQueue> listQueues(String temporaryQueuePrefix) throws JMSException {
		Collection<NevadoQueue> queues;
		ListQueuesResult result;
		try {
			result = getAmazonSQS().listQueues(new ListQueuesRequest().withQueueNamePrefix(temporaryQueuePrefix));
		} catch (AmazonClientException e) {
			throw handleAWSException("Unable to list queues with prefix '" + temporaryQueuePrefix + "'", e);
		}
		queues = new HashSet<NevadoQueue>(result.getQueueUrls().size());
		for (String queueUrl : result.getQueueUrls()) {
			queues.add(new NevadoQueue(queueUrl));
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
			getAmazonSNS().deleteTopic(new DeleteTopicRequest().withTopicArn(getTopicARN(topic)));
		} catch (AmazonClientException e) {
			throw handleAWSException("Unable to delete message topic '" + topic, e);
		}
	}

	@Override
	public Collection<NevadoTopic> listTopics() throws JMSException {
		Collection<NevadoTopic> topics;
		ListTopicsResult result;
		try {
			result = getAmazonSNS().listTopics();
		} catch (AmazonClientException e) {
			throw handleAWSException("Unable to list topics", e);
		}
		topics = new HashSet<NevadoTopic>(result.getTopics().size());
		for (Topic topic : result.getTopics()) {
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
			subscriptionArn = getAmazonSNS().subscribe(
					new SubscribeRequest().withTopicArn(getTopicARN(topic)).withProtocol("sqs").withEndpoint(sqsArn))
					.getSubscriptionArn();
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
			getAmazonSNS().unsubscribe(new UnsubscribeRequest().withSubscriptionArn(topic.getSubscriptionArn()));
		} catch (AmazonClientException e) {
			throw handleAWSException(
					"Unable to subscribe topic " + topic + " with sub ARN " + topic.getSubscriptionArn(), e);
		}
	}
	
	protected String getTopicARN(NevadoTopic topic) throws JMSException {
		if (topic.getArn() == null) {
			CreateTopicResult result;
			try {
				result = getAmazonSNS().createTopic(new CreateTopicRequest(topic.getTopicName()));
			} catch (AmazonClientException e) {
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
		if (e.getCause() != null
				&& (UnknownHostException.class.equals(e.getCause().getClass()) || SSLException.class.equals((e
						.getCause().getClass())))) {
			jmsException = new ResourceAllocationException(exMessage);
		} else if (isSecurityException(e)) {
			jmsException = new JMSSecurityException(exMessage);
		} else {
			jmsException = new JMSException(exMessage);
		}
		return jmsException;
	}
	
	private boolean isSecurityException(AmazonClientException e) {
		if (e instanceof AmazonServiceException) {
			return AWS_ERROR_CODE_AUTHENTICATION.equals(((AmazonServiceException) e).getErrorCode());
		} else {
			return false;
		}
	}
	
}
