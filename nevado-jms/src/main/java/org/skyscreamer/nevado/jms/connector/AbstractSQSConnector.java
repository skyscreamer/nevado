package org.skyscreamer.nevado.jms.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.InvalidMessage;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoProperty;
import org.skyscreamer.nevado.jms.util.MessageIdUtil;
import org.skyscreamer.nevado.jms.util.SerializeUtil;

import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Abstract connector that handles handling of messages and queues independent of the actual implementation.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public abstract class AbstractSQSConnector implements SQSConnector {
    protected static final String AWS_ERROR_CODE_AUTHENTICATION = "InvalidClientTokenId";

    protected final Log _log = LogFactory.getLog(getClass());

    private final long _receiveCheckIntervalMs;
    private final boolean _isAsync;

    protected AbstractSQSConnector(long receiveCheckIntervalMs)
    {
        this(receiveCheckIntervalMs, false);
    }

    protected AbstractSQSConnector(long receiveCheckIntervalMs, boolean isAsync)
    {
        _receiveCheckIntervalMs = receiveCheckIntervalMs;
        _isAsync = isAsync;
    }

    public boolean isAsync() {
        return _isAsync;
    }

    public void sendMessage(NevadoDestination destination, NevadoMessage message) throws JMSException
    {
        if (destination == null)
        {
            throw new NullPointerException();
        }

        // Initialize message as needed
        if (message.isDisableMessageID())
        {
            message.setNevadoProperty(NevadoProperty.DisableMessageID, true);
        }
        if (!message.isDisableTimestamp())
        {
            message.setJMSTimestamp(System.currentTimeMillis());
        }

        if (destination instanceof NevadoQueue)
        {
            if (isAsync() && !message.isDisableMessageID() && message.getJMSMessageID() == null) {
                message.setJMSMessageID(MessageIdUtil.createMessageId());
            }
            String serializedMessage = serializeMessage(message);
            String sqsMessageId = sendSQSMessage((NevadoQueue)destination, serializedMessage);
            if (!message.isDisableMessageID() && message.getJMSMessageID() == null)
            {
                message.setJMSMessageID("ID:" + sqsMessageId);
            }
            _log.info("Sent message to SQS " + sqsMessageId);
        }
        else if (destination instanceof NevadoTopic)
        {
            if (!message.isDisableMessageID() && message.getJMSMessageID() == null)
            {
                message.setJMSMessageID("ID:" + MessageIdUtil.createMessageId());
            }
            String serializedMessage = serializeMessage(message);
            sendSNSMessage((NevadoTopic)destination, serializedMessage);
        }
        else
        {
            throw new IllegalStateException("Invalid destination: " + destination.getClass().getName());
        }
    }

    // TODO - Update this to allow implementation-specific bulk handler.
    public void sendMessages(NevadoDestination destination, List<NevadoMessage> outgoingMessages) throws JMSException {
        for(NevadoMessage message : outgoingMessages)
        {
            sendMessage(destination, message);
        }
    }

    public NevadoMessage receiveMessage(NevadoConnection connection, NevadoDestination destination, long timeoutMs) throws JMSException, InterruptedException {
        long startTimeMs = new Date().getTime();
        SQSQueue sqsQueue = getSQSQueue(destination);
        SQSMessage sqsMessage = receiveSQSMessage(connection, destination, timeoutMs, startTimeMs, sqsQueue);
        if (sqsMessage != null) {
            _log.info("Received message " + sqsMessage.getMessageId());
        }
        return sqsMessage != null ? convertSqsMessage(destination, sqsMessage, false) : null;
    }

    public void deleteMessage(NevadoMessage message) throws JMSException {
        SQSQueue sqsQueue = getSQSQueue(message.getNevadoDestination());
        String sqsReceiptHandle = getSQSReceiptHandle(message);
        sqsQueue.deleteMessage(sqsReceiptHandle);
    }

    public void resetMessage(NevadoMessage message) throws JMSException {
        String sqsReceiptHandle = (String)message.getNevadoProperty(NevadoProperty.SQSReceiptHandle);
        if (sqsReceiptHandle == null)
        {
            throw new JMSException("Message does not contain an SQSReceiptHandle, so cannot be reset.  " +
                    "Did this come from an SQS queue?");
        }
        SQSQueue sqsQueue = getSQSQueue(message.getNevadoDestination());
        sqsQueue.setMessageVisibilityTimeout(sqsReceiptHandle, 0);
    }

    /**
     * Create a queue
     *
     * @param queueName Name of queue to create
     */
    public NevadoQueue createQueue(String queueName) throws JMSException {
        NevadoQueue queue = new NevadoQueue(queueName);
        getSQSQueue(queue);
        return queue;
    }

    @Override
    public void deleteQueue(NevadoQueue queue) throws JMSException {
        SQSQueue sqsQueue = getSQSQueue(queue);
        sqsQueue.deleteQueue();
    }

    protected abstract void sendSNSMessage(NevadoTopic topic, String serializedMessage) throws JMSException;

    protected SQSQueue getSQSQueue(NevadoDestination destination) throws JMSException
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
        return getSQSQueueImpl(queue);
    }

    protected abstract SQSQueue getSQSQueueImpl(NevadoQueue queue) throws JMSException;


    protected SQSMessage receiveSQSMessage(NevadoConnection connection, NevadoDestination destination, long timeoutMs,
                                           long startTimeMs, SQSQueue sqsQueue)
            throws JMSException, InterruptedException {
        SQSMessage sqsMessage;
        while(true) {
            if (connection.isRunning()) {
                sqsMessage = sqsQueue.receiveMessage();
                if (sqsMessage != null && !connection.isRunning()) {
                    // Connection was stopped while the REST call to SQS was being made
                    try {
                        sqsQueue.setMessageVisibilityTimeout(sqsMessage.getReceiptHandle(), 0); // Make it immediately available to the next requestor
                    } catch (JMSException e) {
                        String exMessage = "Unable to reset visibility timeout for message: " + e.getMessage();
                        _log.warn(exMessage, e); // Non-fatal.  Just means the message will disappear until the visibility timeout expires.
                    }
                    sqsMessage = null;
                }
            }
            else {
                _log.debug("Not accepting messages.  Connection is paused or not started.");
                sqsMessage = null;
            }

            // Check for message or timeout
            if (sqsMessage != null || (timeoutMs > -1 && (new Date().getTime() - startTimeMs) >= timeoutMs)) {
                break;
            }

            Thread.sleep(_receiveCheckIntervalMs);
        }
        if (_log.isDebugEnabled())
        {
            _log.debug("Received message: " + ((sqsMessage != null) ? sqsMessage.getMessageBody() : null));
        }
        return sqsMessage;
    }

    protected String sendSQSMessage(NevadoQueue queue, String serializedMessage) throws JMSException
    {
        SQSQueue sqsQueue = getSQSQueue(queue);
        if (_log.isDebugEnabled())
        {
            _log.debug("Sending message: " + serializedMessage);
        }
        return sqsQueue.sendMessage(serializedMessage);
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
            serializedMessage = SerializeUtil.serializeToString(message);
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
     * @throws JMSException Unable to deserializeFromString a single NevadoMessage object from the source
     */
    protected NevadoMessage deserializeMessage(String serializedMessage) throws JMSException {
        Serializable deserializedObject;
        try {
            deserializedObject = SerializeUtil.deserializeFromString(serializedMessage);
        } catch (IOException e) {
            String exMessage = "Unable to deserialized message: " + e.getMessage();
            _log.error(exMessage, e);
            throw new JMSException(exMessage);
        }
        if (deserializedObject == null) {
            throw new JMSException("Deserialized object is null");
        }
        if (!(deserializedObject instanceof NevadoMessage)) {
            throw new JMSException("Expected object of type NevadoMessage, got: "
                    + deserializedObject.getClass().getName());
        }
        return (NevadoMessage)deserializedObject;
    }

    /**
     * Creates the policy for a sqs subscription to sns
     *
     * @param snsArn ARN of SNS topic to subscribe to
     * @param sqsArn ARN of SQS queue subscriber
     * @return Policy rule to create
     */
    protected String getPolicy(String snsArn, String sqsArn) {
        return "{ \n" +
                "    \"Version\":\"2008-10-17\", \n" +
                "    \"Id\":\"" + sqsArn + "\", \n" +
                "    \"Statement\": [ \n" +
                "        { \n" +
                "            \"Sid\":\"" + sqsArn + "/statementId\", \n" +
                "            \"Effect\":\"Allow\", \n" +
                "            \"Principal\":{\"AWS\":\"*\"}, \n" +
                "            \"Action\":\"SQS:SendMessage\", \n" +
                "            \"Resource\": \"" + sqsArn + "\", \n" +
                "            \"Condition\":{ \n" +
                "                \"StringEquals\":{\"aws:SourceArn\":\"" + snsArn + "\"} \n" +
                "            } \n" +
                "        } \n" +
                "    ] \n" +
                "}";
    }

    private String getSQSReceiptHandle(NevadoMessage message) throws JMSException {
        String sqsReceiptHandle = (String)message.getNevadoProperty(NevadoProperty.SQSReceiptHandle);
        if (sqsReceiptHandle == null) {
            throw new JMSException("Invalid null SQS receipt handle");
        }
        return sqsReceiptHandle;
    }
}
