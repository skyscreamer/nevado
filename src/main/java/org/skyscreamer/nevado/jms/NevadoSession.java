package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.message.*;

import javax.jms.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Nevado implementation of the general JMS Session interface.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoSession implements Session, QueueSession, TopicSession {
    public static final String TEMPORARY_QUEUE_PREFIX = "nevado_temp";

    private final Log _log = LogFactory.getLog(getClass());

    private final NevadoConnection _connection;
    private boolean _transacted;
    private int _acknowledgeMode;
    private Integer _overrideJMSDeliveryMode;
    private Long _overrideJMSTTL;
    private Integer _overrideJMSPriority;
    private final Set<TemporaryQueue> _temporaryQueues = new HashSet<TemporaryQueue>();

    protected NevadoSession(NevadoConnection connection, boolean transacted, int acknowledgeMode) {
        _connection = connection;
        _transacted = transacted;
        _acknowledgeMode = acknowledgeMode;
    }

    public BytesMessage createBytesMessage() throws JMSException {
        NevadoBytesMessage message = new NevadoBytesMessage();
        message.setNevadoSession(this);
        return message;
    }

    public MapMessage createMapMessage() throws JMSException {
        NevadoMapMessage message = new NevadoMapMessage();
        message.setNevadoSession(this);
        return message;
    }

    public Message createMessage() throws JMSException {
        NevadoMessage message = new NevadoBlankMessage();
        message.setNevadoSession(this);
        return message;
    }

    public NevadoObjectMessage createObjectMessage() throws JMSException {
        NevadoObjectMessage message = new NevadoObjectMessage();
        message.setNevadoSession(this);
        return message;
    }

    public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException {
        NevadoObjectMessage message = createObjectMessage();
        message.setObject(serializable);
        return message;
    }

    public StreamMessage createStreamMessage() throws JMSException {
        NevadoStreamMessage message = new NevadoStreamMessage();
        message.setNevadoSession(this);
        return message;
    }

    public NevadoTextMessage createTextMessage() throws JMSException {
        NevadoTextMessage message = new NevadoTextMessage();
        message.setNevadoSession(this);
        return message;
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        NevadoTextMessage message = createTextMessage();
        message.setText(text);
        return message;
    }

    public boolean getTransacted() throws JMSException {
        return _transacted;
    }

    public int getAcknowledgeMode() throws JMSException {
        return _acknowledgeMode;
    }

    public void commit() throws JMSException {
        // TODO
    }

    public void rollback() throws JMSException {
        // TODO
    }

    public void close() {
        for(TemporaryQueue temporaryQueue : _temporaryQueues) {
            try {
                temporaryQueue.delete();
            } catch (JMSException e) {
                // Log but continue
                _log.error("Unable to delete temporaryQueue " + temporaryQueue, e);
            }
        }
    }

    public void recover() throws JMSException {
        // TODO
    }

    public MessageListener getMessageListener() throws JMSException {
        return null;  // TODO
    }

    public void setMessageListener(MessageListener messageListener) throws JMSException {
        // TODO
    }

    public void run() {
        // TODO
    }

    public MessageProducer createProducer(Destination destination) throws JMSException {
        return new NevadoMessageProducer(this, NevadoDestination.getInstance(destination));
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return new NevadoMessageConsumer(this, NevadoDestination.getInstance(destination));
    }

    public MessageConsumer createConsumer(Destination destination, String s) throws JMSException {
        return null;  // TODO
    }

    public MessageConsumer createConsumer(Destination destination, String s, boolean b) throws JMSException {
        return null;  // TODO
    }

    public Queue createQueue(String s) throws JMSException {
        return null;  // TODO
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        return null; // TODO
    }

    public QueueReceiver createReceiver(Queue queue, String s) throws JMSException {
        return null; // TODO
    }

    public QueueSender createSender(Queue queue) throws JMSException {
        return null; // TODO
    }

    public Topic createTopic(String s) throws JMSException {
        return null;  // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
        return null; // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic, String s, boolean b) throws JMSException {
        return null; // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s) throws JMSException {
        return null;  // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b) throws JMSException {
        return null;  // TODO
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException {
        return null; // TODO
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return null;  // TODO
    }

    public QueueBrowser createBrowser(Queue queue, String s) throws JMSException {
        return null;  // TODO
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        String tempQueueName = TEMPORARY_QUEUE_PREFIX + UUID.randomUUID();
        NevadoQueue queue = _connection.getSQSConnector().createQueue(tempQueueName);
        NevadoTemporaryQueue temporaryQueue = new NevadoTemporaryQueue(this, queue);
        _temporaryQueues.add(temporaryQueue);
        return temporaryQueue;
    }

    public void deleteTemporaryQueue(NevadoTemporaryQueue temporaryQueue) throws JMSException {
        _connection.getSQSConnector().deleteQueue(temporaryQueue);
        _temporaryQueues.remove(temporaryQueue);
    }
    
    public Collection<TemporaryQueue> listAllTemporaryQueues() throws JMSException {
        Collection<NevadoQueue> queues = _connection.getSQSConnector().listQueues(TEMPORARY_QUEUE_PREFIX);
        Collection<TemporaryQueue> temporaryQueues = new HashSet<TemporaryQueue>(queues.size());
        for(NevadoQueue queue : queues) {
            temporaryQueues.add(new NevadoTemporaryQueue(this, queue));
        }
        return temporaryQueues;
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return null;  // TODO
    }

    public void unsubscribe(String s) throws JMSException {
        // TODO
    }

    public void sendMessage(NevadoDestination destination, NevadoMessage message, boolean disableMessageID,
                            boolean disableTimestamp) throws JMSException {
        if (_overrideJMSDeliveryMode != null) {
            message.setJMSDeliveryMode(_overrideJMSDeliveryMode);
        }
        if (_overrideJMSPriority != null) {
            message.setJMSPriority(_overrideJMSPriority);
        }
        if (_overrideJMSTTL != null) {
            message.setJMSExpiration(_overrideJMSTTL > 0 ? System.currentTimeMillis() + _overrideJMSTTL : 0);
        }
        message.onSend();
        _connection.getSQSConnector().sendMessage(destination, message, disableMessageID, disableTimestamp);
    }

    public Message receiveMessage(NevadoDestination destination, long timeoutMs) throws JMSException {
        NevadoMessage message = getUnfilteredMessage(destination, timeoutMs);

        // Filter expired messages
        while(message != null && message.getJMSExpiration() > 0
                && System.currentTimeMillis() > message.getJMSExpiration())
        {
            message.expire();
            _log.info("Skipped expired message (" + message.getJMSMessageID() + ")");

            message = getUnfilteredMessage(destination, timeoutMs);
        }

        // Set session and destination
        return message;
    }

    private NevadoMessage getUnfilteredMessage(NevadoDestination destination, long timeoutMs) throws JMSException {
        NevadoMessage message = _connection.getSQSConnector().receiveMessage(_connection, destination, timeoutMs);
        if (message != null) {
            message.setNevadoSession(this);
            message.setNevadoDestination(destination);
        }
        return message;
    }

    public void deleteMessage(NevadoMessage message) throws JMSException {
        _connection.getSQSConnector().deleteMessage(message);
    }

    public void setOverrideJMSDeliveryMode(Integer jmsDeliveryMode) {
        _overrideJMSDeliveryMode = jmsDeliveryMode;
    }

    public void setOverrideJMSTTL(Long jmsTTL) {
        _overrideJMSTTL = jmsTTL;
    }

    public void setOverrideJMSPriority(Integer jmsPriority) {
        _overrideJMSPriority = jmsPriority;
    }
}
