package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.destination.*;
import org.skyscreamer.nevado.jms.message.*;
import org.skyscreamer.nevado.jms.util.MessageIdUtil;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.jms.Queue;
import java.io.Serializable;
import java.util.*;

/**
 * Nevado implementation of the general JMS Session interface.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoSession implements Session {
    private final Log _log = LogFactory.getLog(getClass());

    protected boolean _closed = false;
    private final NevadoConnection _connection;
    private boolean _transacted;
    private int _acknowledgeMode;
    private Integer _overrideJMSDeliveryMode;
    private Long _overrideJMSTTL;
    private Integer _overrideJMSPriority;
    private MessageListener _messageListener;
    private final AsyncConsumerRunner _asyncConsumerRunner;
    private final MessageHolder _incomingStagedMessages = new MessageHolder(this);
    private final Map<NevadoDestination, List<NevadoMessage>> _outgoingTxMessages
            = new HashMap<NevadoDestination, List<NevadoMessage>>();
    private final Set<NevadoMessageConsumer> _consumers = new HashSet<NevadoMessageConsumer>();
    private final Set<NevadoMessageProducer> _producers = new HashSet<NevadoMessageProducer>();
    private boolean _TESTING_ONLY_break = false;

    protected NevadoSession(NevadoConnection connection, boolean transacted, int acknowledgeMode)
    {
        _connection = connection;
        _transacted = transacted;
        _acknowledgeMode = acknowledgeMode;
        _asyncConsumerRunner = new AsyncConsumerRunner(_connection);
    }

    @Override
    public BytesMessage createBytesMessage() throws JMSException
    {
        checkClosed();
        NevadoBytesMessage message = new NevadoBytesMessage();
        message.setNevadoSession(this);
        return message;
    }

    @Override
    public MapMessage createMapMessage() throws JMSException
    {
        checkClosed();
        NevadoMapMessage message = new NevadoMapMessage();
        message.setNevadoSession(this);
        return message;
    }

    @Override
    public Message createMessage() throws JMSException
    {
        checkClosed();
        NevadoMessage message = new NevadoBlankMessage();
        message.setNevadoSession(this);
        return message;
    }

    @Override
    public NevadoObjectMessage createObjectMessage() throws JMSException
    {
        checkClosed();
        NevadoObjectMessage message = new NevadoObjectMessage();
        message.setNevadoSession(this);
        return message;
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException
    {
        checkClosed();
        NevadoObjectMessage message = createObjectMessage();
        message.setObject(serializable);
        return message;
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException
    {
        checkClosed();
        NevadoStreamMessage message = new NevadoStreamMessage();
        message.setNevadoSession(this);
        return message;
    }

    @Override
    public NevadoTextMessage createTextMessage() throws JMSException
    {
        checkClosed();
        NevadoTextMessage message = new NevadoTextMessage();
        message.setNevadoSession(this);
        return message;
    }

    @Override
    public TextMessage createTextMessage(String text) throws JMSException
    {
        checkClosed();
        NevadoTextMessage message = createTextMessage();
        message.setText(text);
        return message;
    }

    @Override
    public boolean getTransacted() throws JMSException
    {
        return _transacted;
    }

    @Override
    public int getAcknowledgeMode() throws JMSException
    {
        return _acknowledgeMode;
    }

    // TODO - Think about how to handle a failure during commit (a break mid-way would fool atomicity)
    @Override
    public void commit() throws JMSException
    {
        checkClosed();
        if (!_transacted)
        {
            throw new IllegalStateException("Cannot commit an untransacted session");
        }
        for(NevadoDestination destination : _outgoingTxMessages.keySet())
        {
            List<NevadoMessage> outgoingMessages = _outgoingTxMessages.get(destination);
            _connection.getSQSConnector().sendMessages(destination, outgoingMessages);
        }
        _outgoingTxMessages.clear();
        _incomingStagedMessages.acknowledgeConsumedMessages();
    }

    @Override
    public void rollback() throws JMSException
    {
        checkClosed();
        if (!_transacted)
        {
            throw new IllegalStateException("Cannot rollback an untransacted session");
        }
        _outgoingTxMessages.clear();
        _incomingStagedMessages.reset();
    }

    @Override
    public synchronized void close() throws JMSException
    {
        if (!_closed)
        {
            stop();
            _incomingStagedMessages.close();
            for(NevadoMessageProducer producer : _producers)
            {
                producer.close();
            }
            for(NevadoMessageConsumer consumer : _consumers)
            {
                consumer.close();
            }
            _closed = true;
        }
    }

    @Override
    public void recover() throws JMSException
    {
        checkClosed();
        if (_acknowledgeMode == CLIENT_ACKNOWLEDGE) {
            _incomingStagedMessages.reset();
        }
    }

    @Override
    public MessageListener getMessageListener() throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    @Override
    public void setMessageListener(MessageListener messageListener) throws JMSException
    {
        checkClosed();
        // TODO
    }

    @Override
    public void run()
    {
        // TODO
    }

    @Override
    public NevadoMessageProducer createProducer(Destination destination) throws JMSException
    {
        checkClosed();
        NevadoMessageProducer producer = new NevadoMessageProducer(this, NevadoDestination.getInstance(destination));
        _producers.add(producer);
        return producer;
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination) throws JMSException
    {
        return createConsumer(destination, null);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination, String selector) throws JMSException
    {
        return createConsumer(destination, selector, false);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination, String selector, boolean noLocal) throws JMSException
    {
        checkClosed();
        checkValidDestination(destination);
        NevadoMessageConsumer consumer = new NevadoMessageConsumer(this, NevadoDestination.getInstance(destination),
                selector, noLocal);
        _asyncConsumerRunner.addAsyncConsumer(consumer);
        _consumers.add(consumer);
        return consumer;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String s) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    @Override
    public NevadoTemporaryQueue createTemporaryQueue() throws JMSException
    {
        checkClosed();
        return _connection.createTemporaryQueue();
    }

    @Override
    public NevadoQueue createQueue(String name) throws JMSException
    {
        if (!NevadoProviderQueuePrefix.isValidQueueName(name))
        {
            throw new InvalidDestinationException("Queue name is not valid: " + name);
        }
        return createInternalQueue(name);
    }

    protected NevadoQueue createInternalQueue(String name) throws JMSException
    {
        checkClosed();
        return new NevadoQueue(name);
    }

    @Override
    public NevadoTopic createTopic(String name) throws JMSException
    {
        checkClosed();
        return new NevadoTopic(name);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException
    {
        checkClosed();
        return createDurableSubscriber(topic, name, null, false);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String selector, boolean noLocal) throws JMSException
    {
        checkClosed();
        checkValidDestination(topic);
        String queueName = getDurableEndpointQueueName(name);
        if (hasActiveDurableSubscriber(queueName))
        {
            throw new JMSException("There is already a durable subscriber with name " + name);
        }
        NevadoMessageConsumer consumer = new NevadoMessageConsumer(this, NevadoTopic.getInstance(topic), name, selector,
                noLocal);
        _asyncConsumerRunner.addAsyncConsumer(consumer);
        _consumers.add(consumer);
        return consumer;
    }

    @Override
    public NevadoTemporaryTopic createTemporaryTopic() throws JMSException
    {
        checkClosed();
        return _connection.createTemporaryTopic();
    }

    @Override
    public void unsubscribe(String name) throws JMSException
    {
        checkClosed();

        String queueName = getDurableEndpointQueueName(name);
        if (hasActiveDurableSubscriber(queueName))
        {
            throw new JMSException("Cannot unsubscribe durable topic-subscription '"
                    + name + "': There is an active TopicSubscriber");
        }

        // Check for unacknowledged/uncommitted messaages
        for (NevadoMessage message : _incomingStagedMessages.getConsumedMessages())
        {
            if (message.getJMSDestination() instanceof NevadoTopic)
            {
                NevadoTopic topic = (NevadoTopic)message.getJMSDestination();
                if (topic.isDurable() && queueName.equals(topic.getTopicEndpoint().getQueueName()))
                {
                    throw new JMSException("Cannot unsubscribe durable topic-subscription '"
                            + name + "': There is an unacknowledged or uncommitted message from the topic");
                }
            }
        }

        NevadoQueue durableQueue = new NevadoQueue(queueName);
        _connection.getSQSConnector().unsubscribeDurableQueueFromTopic(durableQueue);
        _connection.getSQSConnector().deleteQueue(durableQueue);
    }

    protected String getDurableEndpointQueueName(String durableSubscriptionName) {
        String queueName = NevadoProviderQueuePrefix.DURABLE_SUBSCRIPTION_PREFIX + durableSubscriptionName;
        if (_connection.getClientID() != null)
        {
            queueName += "_client-" + _connection.getClientID() + "";
        }
        return queueName;
    }

    private boolean hasActiveDurableSubscriber(String queueName) throws JMSException {
        for(NevadoMessageConsumer consumer : _consumers)
        {
            if (consumer.isClosed() || consumer.getDestination() == null
                    || !(consumer.getDestination() instanceof NevadoTopic))
            {
                continue;
            }

            NevadoTopic topic = (NevadoTopic)consumer.getDestination();
            if (topic.isDurable() && queueName.equals(topic.getTopicEndpoint().getQueueName()))
            {
                return true;
            }
        }
        return false;
    }

    protected void sendMessage(NevadoDestination destination, NevadoMessage message) throws JMSException
    {
        if (destination == null)
        {
            throw new NullPointerException("Destination is null");
        }
        if (destination instanceof NevadoTopic)
        {
            message.setNevadoProperty(NevadoProperty.ConnectionID, _connection.getConnectionID());
        }
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
            // We aren't calling SQS yet, so we'll need to create our own message ID
            if (!message.isDisableMessageID())
            {
                message.setJMSMessageID(MessageIdUtil.createMessageId());
            }
            _outgoingTxMessages.get(destination).add(message.copyOf());
        }
        else
        {
            _connection.getSQSConnector().sendMessage(destination, message);
        }
    }

    protected NevadoMessage receiveMessage(NevadoDestination destination, long timeoutMs, boolean noLocal)
            throws JMSException
    {
        testBreak();
        long startTime = System.currentTimeMillis();
        NevadoMessage message = null;
        boolean firstPass = true;
        long elapsed = 0;

        while(firstPass
                || (message == null
                    && (timeoutMs < 0 || (elapsed = System.currentTimeMillis() - startTime) < timeoutMs)))
        {
            firstPass = false;
            long adjustedTimeout = timeoutMs < 0 ? timeoutMs : (timeoutMs - elapsed);
            message = getUnfilteredMessage(destination, adjustedTimeout);

            // Filter expired messages
            if (message != null && message.getJMSExpiration() > 0
                    && System.currentTimeMillis() > message.getJMSExpiration())
            {
                message.expire();
                _log.info("Skipped expired message (" + message.getJMSMessageID() + ")");
                message = null;
            }

            // Filter noLocal matches
            if (message != null && destination instanceof NevadoTopic && noLocal && _connection.getConnectionID()
                    .equals(message.getNevadoProperty(NevadoProperty.ConnectionID)))
            {
                deleteMessage(message);
                message = null;
            }
        }

        // Set session and destination
        return message;
    }

    private NevadoMessage getUnfilteredMessage(NevadoDestination destination, long timeoutMs)
            throws JMSException
    {
        NevadoMessage message = null;

        // First check the holder in case there was a recover or rollback
        if (_acknowledgeMode == CLIENT_ACKNOWLEDGE || _transacted)
        {
            message = _incomingStagedMessages.getNextMessage(destination);
        }

        // Else grab a message from the queue
        if (message == null)
        {
            message = _connection.getSQSConnector().receiveMessage(_connection, destination, timeoutMs);

            // Hold onto messages in a transaction or in CLIENT_ACKNOWLEDGE mode
            if (message != null && (_acknowledgeMode == CLIENT_ACKNOWLEDGE || _transacted))
            {
                _incomingStagedMessages.add(destination, message);
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

    public void acknowledgeMessage(NevadoMessage message) throws JMSException
    {
        checkClosed();
        if (this != message.getNevadoSession())
        {
            throw new IllegalStateException("Session should only acknowledge its own messages");
        }
        if (!_transacted) {
            if (_acknowledgeMode == CLIENT_ACKNOWLEDGE)
            {
                _incomingStagedMessages.acknowledgeConsumedMessages();
            }
            else
            {
                deleteMessage(message);
            }
        }
    }

    public void expireMessage(NevadoMessage message) throws JMSException
    {
        checkClosed();
        if (this != message.getNevadoSession())
        {
            throw new IllegalStateException("Session should only expire its own messages");
        }
        deleteMessage(message);
    }

    protected void deleteMessage(NevadoMessage... messages) throws JMSException
    {
        for(NevadoMessage message : messages)
        {
            _connection.getSQSConnector().deleteMessage(message);
        }
    }

    protected void resetMessage(NevadoMessage... messages) throws JMSException
    {
        for(NevadoMessage message : messages)
        {
            _connection.getSQSConnector().resetMessage(message);
        }
    }

    public void setOverrideJMSDeliveryMode(Integer jmsDeliveryMode)
    {
        _overrideJMSDeliveryMode = jmsDeliveryMode;
    }

    public void setOverrideJMSTTL(Long jmsTTL)
    {
        _overrideJMSTTL = jmsTTL;
    }

    public void setOverrideJMSPriority(Integer jmsPriority)
    {
        _overrideJMSPriority = jmsPriority;
    }

    public boolean isClosed() {
        return _closed;
    }

    protected NevadoConnection getConnection()
    {
        return _connection;
    }

    protected synchronized void start()
    {
        _asyncConsumerRunner.start();
    }

    protected synchronized void stop() throws JMSException
    {
        try {
            _asyncConsumerRunner.stop();
        } catch (InterruptedException e) {
            String exMessage = "Session threads may not have closed yet: " + e.getMessage();
            _log.error(exMessage, e);
            throw new JMSException(exMessage);
        }
    }

    protected void checkClosed() throws IllegalStateException
    {
        if (_closed)
        {
            throw new IllegalStateException("This session has been closed");
        }
    }

    private void checkValidDestination(Destination destination) throws JMSException
    {
        if (destination instanceof TemporaryQueue || destination instanceof TemporaryTopic)
        {
            if (!_connection.ownsTemporaryDestination(destination))
            {
                throw new InvalidDestinationException("Consumers for temporary destinations cannot be created " +
                        "outside of the connection where the destination was created.");
            }
        }
    }

    protected void setBreakSessionForTesting(boolean value)
    {
        _TESTING_ONLY_break = value;
    }

    private void testBreak() throws JMSException {
        if (_TESTING_ONLY_break)
        {
            throw new JMSException("SESSION DELIBERATELY THROWING EXCEPTION - FOR TESTING MODE ONLY");
        }
    }
}
