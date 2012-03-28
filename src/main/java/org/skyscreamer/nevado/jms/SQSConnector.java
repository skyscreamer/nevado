package org.skyscreamer.nevado.jms;

import com.xerox.amazonws.sqs2.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.InvalidMessage;
import org.skyscreamer.nevado.jms.message.NevadoProperty;
import org.skyscreamer.nevado.jms.util.SerializeStringUtil;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/24/12
 * Time: 11:57 AM
 *
 * TODO: Put the check interval and optional back-off strategy into the NevadoDestinations so they can be
 *       configured on a per-destination basis.
 */
public class SQSConnector {
    private final Log _log = LogFactory.getLog(SQSConnector.class);

    private final QueueService _queueService;
    private final long _receiveCheckIntervalMs;

    public SQSConnector(String awsAccessKey, String awsSecretKey) {
        _queueService = new QueueService(awsAccessKey, awsSecretKey);
        _receiveCheckIntervalMs = 1000;
    }

    public SQSConnector(String awsAccessKey, String awsSecretKey, long receiveCheckIntervalMs) {
        _log.warn("Reducing the receiveCheckInterval will increase your AWS costs.  " +
                "Amazon charges each time a check is made: http://aws.amazon.com/sqs/pricing/");
        _queueService = new QueueService(awsAccessKey, awsAccessKey);
        _receiveCheckIntervalMs = receiveCheckIntervalMs;
    }

    public void sendMessage(NevadoDestination destination, NevadoMessage message, boolean disableMessageID) throws JMSException {
        MessageQueue sqsQueue = getSQSQueue(destination);
        String serializedMessage = serializeMessage(message);
        String sqsMessageId = sendSQSMessage(destination, sqsQueue, serializedMessage);
        if (!disableMessageID) {
            message.setJMSMessageID("ID:" + sqsMessageId);
        }
        else {
            message.setNevadoProperty(NevadoProperty.DisableMessageID, true);
        }
        _log.info("Sent message " + sqsMessageId);
    }

    public NevadoMessage receiveMessage(NevadoDestination destination, long timeoutMs) throws JMSException {
        long startTimeMs = new Date().getTime();
        MessageQueue sqsQueue = getSQSQueue(destination);
        Message sqsMessage = receiveSQSMessage(destination, timeoutMs, startTimeMs, sqsQueue);
        if (sqsMessage != null) {
            _log.info("Received message " + sqsMessage.getMessageId());
        }
        return sqsMessage != null ? convertSqsMessage(sqsMessage) : null;
    }

    public void deleteMessage(NevadoMessage message) throws JMSException {
        MessageQueue sqsQueue = getSQSQueue(message.getNevadoDestination());
        String sqsReceiptHandle = getSQSReceiptHandle(message);
        deleteSQSMessage(message, sqsQueue, sqsReceiptHandle);
    }

    private void deleteSQSMessage(NevadoMessage message, MessageQueue sqsQueue, String sqsReceiptHandle) throws JMSException {
        try {
            sqsQueue.deleteMessage(sqsReceiptHandle);
        } catch (SQSException e) {
            String exMessage = "Unable to delete message (" + message.getJMSMessageID() + ") with receipt handle " + sqsReceiptHandle;
            _log.error(exMessage, e);
            throw new JMSException(exMessage);
        }
    }

    private String getSQSReceiptHandle(NevadoMessage message) throws JMSException {
        String sqsReceiptHandle = (String)message.getNevadoProperty(NevadoProperty.SQSReceiptHandle);
        if (sqsReceiptHandle == null) {
            throw new JMSException("Invalid null SQS receipt handle");
        }
        return sqsReceiptHandle;
    }


    private NevadoMessage convertSqsMessage(Message sqsMessage) throws JMSException {
        NevadoMessage message;
        try {
            message = deserializeMessage(sqsMessage.getMessageBody());
        } catch (JMSException e) {
            message = new InvalidMessage(e);
        }

        if (!message.nevadoPropertyExists(NevadoProperty.DisableMessageID)
                || !(Boolean)message.getNevadoProperty(NevadoProperty.DisableMessageID))
        {
            message.setJMSMessageID("ID:" + sqsMessage.getMessageId());
        }
        message.setNevadoProperty(NevadoProperty.SQSReceiptHandle, sqsMessage.getReceiptHandle());

        return message;
    }

    private Message receiveSQSMessage(NevadoDestination destination, long timeoutMs, long startTimeMs, MessageQueue sqsQueue) throws JMSException {
        Message sqsMessage;
        while(true) {
            try {
                sqsMessage = sqsQueue.receiveMessage();
            } catch (SQSException e) {
                String exMessage = "Unable to reveive message from '" + destination + "': " + e.getMessage();
                _log.error(exMessage, e);
                throw new JMSException(exMessage);
            }

            // Check for message or timeout
            if (sqsMessage != null || (timeoutMs > -1 && (new Date().getTime() - startTimeMs) >= timeoutMs)) {
                break;
            }
        }
        return sqsMessage;
    }

    private String sendSQSMessage(NevadoDestination destination, MessageQueue sqsQueue, String serializedMessage) throws JMSException {
        try {
            return sqsQueue.sendMessage(serializedMessage);
        } catch (SQSException e) {
            String exMessage = "Unable to send message to queue '" + destination + "': " + e.getMessage();
            _log.error(exMessage, e);
            throw new JMSException(exMessage);
        }
    }

    /**
     * Serialize a NevadoMessage object into the body of an SQS message into a bae64 string.
     *
     * @param message A NevadoMessage object
     * @return A string-serialized encoding of the message
     * @throws JMSException Unable to serialize the message
     */
    protected String serializeMessage(NevadoMessage message) throws JMSException {
        String serializedMessage;
        try {
            serializedMessage = SerializeStringUtil.serialize(message);
        } catch (IOException e) {
            String exMessage = "Unable to serialize message of type " + message.getClass().getName() + ": " + e.getMessage();
            _log.error(exMessage, e);
            throw new JMSException(exMessage);
        }
        return serializedMessage;
    }

    /**
     * Deserializes the body of an SQS message into a NevadoMessage object.
     *
     * @param serializedMessage String-serialized NevadoMessage
     * @return A deserialized NevadoMessage object
     * @throws JMSException Unable to deserialize a single NevadoMessage object from the source
     */
    protected NevadoMessage deserializeMessage(String serializedMessage) throws JMSException {
        Object[] deserializedObjects;
        try {
            deserializedObjects = SerializeStringUtil.deserialize(serializedMessage);
        } catch (IOException e) {
            String exMessage = "Unable to deserialized message: " + e.getMessage();
            _log.error(exMessage, e);
            throw new JMSException(exMessage);
        }
        if (deserializedObjects.length != 1) {
            throw new JMSException("Expected 1 deserialized object, got: " + deserializedObjects.length);
        }
        if (deserializedObjects[0] == null) {
            throw new JMSException("Deserialized object is null");
        }
        if (!(deserializedObjects[0] instanceof NevadoMessage)) {
            throw new JMSException("Expected object of type NevadoMessage, got: "
                    + deserializedObjects[0].getClass().getName());
        }
        return (NevadoMessage)deserializedObjects[0];
    }

    private MessageQueue getSQSQueue(NevadoDestination destination) throws JMSException {
        if (destination == null) {
            throw new JMSException("Destination is null");
        }

        MessageQueue sqsQueue;
        try {
            sqsQueue = _queueService.getOrCreateMessageQueue(destination.getName());
        } catch (SQSException e) {
            String exMessage = "Unable to get message queue '" + destination + "': " + e.getMessage();
            _log.error(exMessage, e);
            throw new JMSException(exMessage);
        }
        return sqsQueue;
    }
}
