package org.skyscreamer.nevado.jms.connector.typica;

import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.QueueAttribute;
import com.xerox.amazonws.sqs2.SQSException;
import org.skyscreamer.nevado.jms.connector.SQSQueue;

import javax.jms.JMSException;
import java.util.Map;

/**
 * Representation of a Typica queue
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
class TypicaSQSQueue implements SQSQueue {
    private final TypicaSQSConnector _typicaSQSConnector;
    private final MessageQueue _sqsQueue;

    public TypicaSQSQueue(TypicaSQSConnector typicaSQSConnector, MessageQueue sqsQueue) {
        _typicaSQSConnector = typicaSQSConnector;
        _sqsQueue = sqsQueue;
    }

    @Override
    public void deleteQueue() throws JMSException {
        try {
            _sqsQueue.deleteQueue();
        } catch (SQSException e) {
            throw _typicaSQSConnector.handleAWSException("Unable to delete message queue '" + _sqsQueue.getUrl(), e);
        }
    }

    @Override
    public String sendMessage(String serializedMessage) throws JMSException {
        try {
            return _sqsQueue.sendMessage(serializedMessage);
        } catch (SQSException e) {
            throw _typicaSQSConnector.handleAWSException("Unable to send message to queue " + _sqsQueue.getUrl(), e);
        }
    }

    @Override
    public void setMessageVisibilityTimeout(String sqsReceiptHandle, int timeout) throws JMSException {
        try {
            _sqsQueue.setMessageVisibilityTimeout(sqsReceiptHandle, timeout);
        } catch (SQSException e) {
            throw _typicaSQSConnector.handleAWSException("Unable to reset message visibility for message "
                    + "with receipt handle " + sqsReceiptHandle, e);
        }
    }

    @Override
    public String getQueueARN() throws JMSException {
        Map<String,String> queueAttrMap = null;
        try {
            queueAttrMap = _sqsQueue.getQueueAttributes(QueueAttribute.QUEUE_ARN);
        } catch (SQSException e) {
            throw _typicaSQSConnector.handleAWSException("Unable to get queue ARN for queue " + _sqsQueue.getUrl(), e);
        }
        return queueAttrMap.get(QueueAttribute.QUEUE_ARN.queryAttribute());
    }

    @Override
    public void setPolicy(String policy) throws JMSException {
        try {
            _sqsQueue.setQueueAttribute("Policy", policy);
        } catch (SQSException e) {
            throw _typicaSQSConnector.handleAWSException("Unable to set policy", e);
        }
    }

    @Override
    public void deleteMessage(String sqsReceiptHandle) throws JMSException {
        try {
            _sqsQueue.deleteMessage(sqsReceiptHandle);
        } catch (SQSException e) {
            throw _typicaSQSConnector.handleAWSException("Unable to delete message with receipt handle " + sqsReceiptHandle, e);
        }
    }

    @Override
    public TypicaSQSMessage receiveMessage() throws JMSException {
        TypicaSQSMessage sqsMessage;
        try {
            Message message = _sqsQueue.receiveMessage();
            sqsMessage = (message != null) ? new TypicaSQSMessage(message) : null;
        } catch (SQSException e) {
            throw _typicaSQSConnector.handleAWSException("Unable to retrieve message from queue " + _sqsQueue.getUrl(), e);
        }
        return sqsMessage;
    }
}
