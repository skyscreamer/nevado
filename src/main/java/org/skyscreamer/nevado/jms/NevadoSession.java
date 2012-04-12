package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.message.*;
import org.skyscreamer.nevado.jms.util.MessageHolder;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.jms.Queue;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Nevado implementation of the general JMS Session interface.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoSession implements Session, QueueSession, TopicSession {
    public static final String TEMPORARY_QUEUE_PREFIX = "nevado_temp";

    private final Log _log = LogFactory.getLog(getClass());

    protected boolean _closed = false;
    private final NevadoConnection _connection;
    private boolean _transacted;
    private int _acknowledgeMode;
    private Integer _overrideJMSDeliveryMode;
    private Long _overrideJMSTTL;
    private Integer _overrideJMSPriority;
    private final Set<TemporaryQueue> _temporaryQueues = new HashSet<TemporaryQueue>();
    private MessageListener _messageListener;
    private final NevadoSessionExecutor _asyncConsumerRunner = new NevadoSessionExecutor(this);
    private List<NevadoMessageConsumer> _messageConsumers = new CopyOnWriteArrayList<NevadoMessageConsumer>();
    private final Map<Destination, MessageHolder> _incomingStagedMessages = new HashMap<Destination, MessageHolder>();
    private final Map<Destination, List<NevadoMessage>> _outgoingTxMessages = new HashMap<Destination, List<NevadoMessage>>();

    protected NevadoSession(NevadoConnection connection, boolean transacted, int acknowledgeMode) {
        _connection = connection;
        _transacted = transacted;
        _acknowledgeMode = acknowledgeMode;
    }

    public BytesMessage createBytesMessage() throws JMSException {
        checkClosed();
        NevadoBytesMessage message = new NevadoBytesMessage();
        message.setNevadoSession(this);
        return message;
    }

    public MapMessage createMapMessage() throws JMSException {
        checkClosed();
        NevadoMapMessage message = new NevadoMapMessage();
        message.setNevadoSession(this);
        return message;
    }

    public Message createMessage() throws JMSException {
        checkClosed();
        NevadoMessage message = new NevadoBlankMessage();
        message.setNevadoSession(this);
        return message;
    }

    public NevadoObjectMessage createObjectMessage() throws JMSException {
        checkClosed();
        NevadoObjectMessage message = new NevadoObjectMessage();
        message.setNevadoSession(this);
        return message;
    }

    public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException {
        checkClosed();
        NevadoObjectMessage message = createObjectMessage();
        message.setObject(serializable);
        return message;
    }

    public StreamMessage createStreamMessage() throws JMSException {
        checkClosed();
        NevadoStreamMessage message = new NevadoStreamMessage();
        message.setNevadoSession(this);
        return message;
    }

    public NevadoTextMessage createTextMessage() throws JMSException {
        checkClosed();
        NevadoTextMessage message = new NevadoTextMessage();
        message.setNevadoSession(this);
        return message;
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        checkClosed();
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

    // TODO - Think about how to handle a failure during commit
    public void commit() throws JMSException {
        checkClosed();
        if (_transacted)
        {
            for(Destination destination : _outgoingTxMessages.keySet())
            {
                List<NevadoMessage> outgoingMessages = _outgoingTxMessages.get(destination);
                _connection.getSQSConnector().sendMessages(destination, outgoingMessages);
            }
            _outgoingTxMessages.clear();

            for(Destination destination : _incomingStagedMessages.keySet())
            {
                for(NevadoMessage msg : _incomingStagedMessages.get(destination).getConsumedMessages())
                {
                    deleteMessage(msg);
                    msg.setAcknowledged(true);
                }
                for(NevadoMessage msg : _incomingStagedMessages.get(destination).getUnconsumedMessages())
                {
                    resetMessage(msg);
                }
            }
            _incomingStagedMessages.clear();
        }
    }

    public void rollback() throws JMSException {
        checkClosed();
        if (_transacted)
        {
            _outgoingTxMessages.clear();
            for(MessageHolder messageHolder : _incomingStagedMessages.values()) {
                messageHolder.reset();
            }
        }
    }

    public void close() {
        if (_closed)
        {
            return;
        }
        _closed = true;
        stop();
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
        checkClosed();
        if (_acknowledgeMode == CLIENT_ACKNOWLEDGE) {
            for(MessageHolder messageHolder : _incomingStagedMessages.values()) {
                messageHolder.reset();
            }
        }
    }

    public MessageListener getMessageListener() throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public void setMessageListener(MessageListener messageListener) throws JMSException {
        checkClosed();
        // TODO
    }

    public void run() {
        // TODO
    }

    public MessageProducer createProducer(Destination destination) throws JMSException {
        checkClosed();
        return new NevadoMessageProducer(this, NevadoDestination.getInstance(destination));
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        checkClosed();
        NevadoMessageConsumer consumer = new NevadoMessageConsumer(this, NevadoDestination.getInstance(destination));
        _messageConsumers.add(consumer);
        return consumer;
    }

    public MessageConsumer createConsumer(Destination destination, String s) throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public MessageConsumer createConsumer(Destination destination, String s, boolean b) throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public Queue createQueue(String s) throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        checkClosed();
        return null; // TODO
    }

    public QueueReceiver createReceiver(Queue queue, String s) throws JMSException {
        checkClosed();
        return null; // TODO
    }

    public QueueSender createSender(Queue queue) throws JMSException {
        checkClosed();
        return null; // TODO
    }

    public Topic createTopic(String s) throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
        checkClosed();
        return null; // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic, String s, boolean b) throws JMSException {
        checkClosed();
        return null; // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s) throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b) throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException {
        checkClosed();
        return null; // TODO
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        checkClosed();
        return null;  // TODO
    }

    public QueueBrowser createBrowser(Queue queue, String s) throws JMSException {
        checkClosed();
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
        checkClosed();
        return null;  // TODO
    }

    public void unsubscribe(String s) throws JMSException {
        checkClosed();
        // TODO
    }

    public void sendMessage(NevadoDestination destination, NevadoMessage message) throws JMSException {
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
        if (_transacted)
        {
            if (!_outgoingTxMessages.containsKey(destination))
            {
                _outgoingTxMessages.put(destination, new ArrayList<NevadoMessage>());
            }
            _outgoingTxMessages.get(destination).add(message.copyOf());
        }
        else
        {
            _connection.getSQSConnector().sendMessage(destination, message);
        }
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
        NevadoMessage message = null;

        // First check the holder in case there was a recover or rollback
        if (_acknowledgeMode == CLIENT_ACKNOWLEDGE || _transacted)
        {
            if (_incomingStagedMessages.containsKey(destination))
            {
                MessageHolder messageHolder = _incomingStagedMessages.get(destination);
                message = messageHolder.getNextMessage();
            }
        }

        // Else grab a message from the queue
        if (message == null)
        {
            message = _connection.getSQSConnector().receiveMessage(_connection, destination, timeoutMs);

            // Hold onto messages in a transaction or in CLIENT_ACKNOWLEDGE mode
            if (message != null && (_acknowledgeMode == CLIENT_ACKNOWLEDGE || _transacted))
            {
                if (!_incomingStagedMessages.containsKey(destination))
                {
                    _incomingStagedMessages.put(destination, new MessageHolder());
                }
                _incomingStagedMessages.get(destination).add(message);
            }
        }

        // If we've got a message decorate it appropriately
        if (message != null) {
            message.setNevadoSession(this);
            message.setNevadoDestination(destination);
            if (message.propertyExists(JMSXProperty.JMSXDeliveryCount + "")) {
                int redeliveryCount = (Integer)message.getJMSXProperty(JMSXProperty.JMSXDeliveryCount);
                ++redeliveryCount;
                message.setJMSXProperty(JMSXProperty.JMSXDeliveryCount, redeliveryCount);
                message.setJMSRedelivered(true);
            }
            else {
                message.setJMSXProperty(JMSXProperty.JMSXDeliveryCount, 1);
            }
        }
        return message;
    }

    public void acknowledgeMessage(NevadoMessage message) throws JMSException {
        if (!_transacted) {
            if (_acknowledgeMode == CLIENT_ACKNOWLEDGE)
            {
                for(NevadoMessage msg : _incomingStagedMessages.get(message.getNevadoDestination()).getConsumedMessages())
                {
                    deleteMessage(msg);
                    msg.setAcknowledged(true);
                }
                _incomingStagedMessages.clear();
            }
            else
            {
                deleteMessage(message);
            }
        }
    }

    public void expireMessage(NevadoMessage message) throws JMSException {
        deleteMessage(message);
    }

    private void deleteMessage(NevadoMessage message) throws JMSException {
        _connection.getSQSConnector().deleteMessage(message);
    }

    private void resetMessage(NevadoMessage message) throws JMSException
    {
        _connection.getSQSConnector().returnMessageToQueue(message);
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

    protected NevadoConnection getConnection() {
        return _connection;
    }

    protected List<NevadoMessageConsumer> getMessageConsumers() {
        return _messageConsumers;
    }

    protected synchronized void start() {
        if (!_asyncConsumerRunner.isRunning())
        {
            _asyncConsumerRunner.start();
        }
    }

    protected synchronized void stop() {
        if (_asyncConsumerRunner.isRunning())
        {
            _asyncConsumerRunner.stop();
        }
    }

    private void checkClosed() throws IllegalStateException {
        if (_closed)
        {
            throw new IllegalStateException("This session has been closed");
        }
    }
}
