package org.skyscreamer.nevado.jms.connector.amazonaws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.model.*;
import org.skyscreamer.nevado.jms.connector.SQSQueue;

import javax.jms.JMSException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Representation of an AmazonAWS queue
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class AmazonAwsSQSQueue implements SQSQueue {
    public static final String ATTRIBUTE_QUEUE_ARN = "QueueArn";
    public static final String ATTRIBUTE_POLICY = "Policy";
    private final String _queueUrl;
    private final AmazonAwsSQSConnector _amazonAwsSQSConnector;

    public AmazonAwsSQSQueue(AmazonAwsSQSConnector amazonAwsSQSConnector, String queueUrl) {
        _amazonAwsSQSConnector = amazonAwsSQSConnector;
        _queueUrl = queueUrl;
    }

    @Override
    public String sendMessage(String serializedMessage) throws JMSException {
        SendMessageResult result;
        try {
            result = _amazonAwsSQSConnector.getAmazonSQS().sendMessage(new SendMessageRequest(_queueUrl, serializedMessage));
        }
        catch (AmazonServiceException e) {
            throw _amazonAwsSQSConnector.handleAWSException("Unable to send message to queue " + _queueUrl, e);
        }
        return result.getMessageId();
    }

    @Override
    public void setMessageVisibilityTimeout(String sqsReceiptHandle, int timeout) throws JMSException {
        try {
            _amazonAwsSQSConnector.getAmazonSQS().changeMessageVisibility(new ChangeMessageVisibilityRequest(_queueUrl, sqsReceiptHandle, timeout));
        }
        catch (AmazonServiceException e) {
            throw _amazonAwsSQSConnector.handleAWSException("Unable to reset message visibility for message "
                    + "with receipt handle " + sqsReceiptHandle, e);
        }
    }

    @Override
    public String getQueueARN() throws JMSException {
        GetQueueAttributesRequest request = new GetQueueAttributesRequest(_queueUrl).withAttributeNames(ATTRIBUTE_QUEUE_ARN);
        Map<String, String> queueAttributes;
        try {
            queueAttributes = _amazonAwsSQSConnector.getAmazonSQS().getQueueAttributes(request).getAttributes();
        }
        catch (AmazonServiceException e) {
            throw _amazonAwsSQSConnector.handleAWSException("Unable to get queue ARN for queue " + _queueUrl, e);
        }
        return queueAttributes.get(ATTRIBUTE_QUEUE_ARN);
    }

    @Override
    public void setPolicy(String policy) throws JMSException {
        try {
            _amazonAwsSQSConnector.getAmazonSQS().setQueueAttributes(new SetQueueAttributesRequest(_queueUrl, Collections.singletonMap(ATTRIBUTE_POLICY, policy)));
        } catch (AmazonServiceException e) {
            throw _amazonAwsSQSConnector.handleAWSException("Unable to set policy", e);
        }
    }

    @Override
    public void deleteMessage(String sqsReceiptHandle) throws JMSException {
        try {
            _amazonAwsSQSConnector.getAmazonSQS().deleteMessage(new DeleteMessageRequest(_queueUrl, sqsReceiptHandle));
        } catch (AmazonServiceException e) {
            throw _amazonAwsSQSConnector.handleAWSException("Unable to delete message with receipt handle " + sqsReceiptHandle, e);
        }
    }

    @Override
    public AmazonAwsSQSMessage receiveMessage() throws JMSException {
        AmazonAwsSQSMessage sqsMessage;
        try {
            ReceiveMessageResult result = _amazonAwsSQSConnector.getAmazonSQS().receiveMessage(new ReceiveMessageRequest(_queueUrl));
            List<Message> messages = result.getMessages();
            sqsMessage = (messages != null && messages.size() > 0) ? new AmazonAwsSQSMessage(messages.get(0)) : null;
        } catch (AmazonServiceException e) {
            throw _amazonAwsSQSConnector.handleAWSException("Unable to retrieve message from queue " + _queueUrl, e);
        }
        return sqsMessage;
    }

    @Override
    public void deleteQueue() throws JMSException {
        try {
            _amazonAwsSQSConnector.getAmazonSQS().deleteQueue(new DeleteQueueRequest(_queueUrl));
        } catch (AmazonServiceException e) {
            throw _amazonAwsSQSConnector.handleAWSException("Unable to delete message queue '" + _queueUrl, e);
        }
    }

}
