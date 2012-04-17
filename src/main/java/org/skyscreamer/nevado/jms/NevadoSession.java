package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.*;

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

    public BytesMessage createBytesMessage() throws JMSException
    {
        checkClosed();
        NevadoBytesMessage message = new NevadoBytesMessage();
        message.setNevadoSession(this);
        return message;
    }

    public MapMessage createMapMessage() throws JMSException
    {
        checkClosed();
        NevadoMapMessage message = new NevadoMapMessage();
        message.setNevadoSession(this);
        return message;
    }

    public Message createMessage() throws JMSException
    {
        checkClosed();
        NevadoMessage message = new NevadoBlankMessage();
        message.setNevadoSession(this);
        return message;
    }

    public NevadoObjectMessage createObjectMessage() throws JMSException
    {
        checkClosed();
        NevadoObjectMessage message = new NevadoObjectMessage();
        message.setNevadoSession(this);
        return message;
    }

    public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException
    {
        checkClosed();
        NevadoObjectMessage message = createObjectMessage();
        message.setObject(serializable);
        return message;
    }

    public StreamMessage createStreamMessage() throws JMSException
    {
        checkClosed();
        NevadoStreamMessage message = new NevadoStreamMessage();
        message.setNevadoSession(this);
        return message;
    }

    public NevadoTextMessage createTextMessage() throws JMSException
    {
        checkClosed();
        NevadoTextMessage message = new NevadoTextMessage();
        message.setNevadoSession(this);
        return message;
    }

    public TextMessage createTextMessage(String text) throws JMSException
    {
        checkClosed();
        NevadoTextMessage message = createTextMessage();
        message.setText(text);
        return message;
    }

    public boolean getTransacted() throws JMSException
    {
        return _transacted;
    }

    public int getAcknowledgeMode() throws JMSException
    {
        return _acknowledgeMode;
    }

    // TODO - Think about how to handle a failure during commit (a break mid-way would fool atomicity)
    public void commit() throws JMSException
    {
        checkClosed();
        if (_transacted)
        {
            for(NevadoDestination destination : _outgoingTxMessages.keySet())
            {
                List<NevadoMessage> outgoingMessages = _outgoingTxMessages.get(destination);
                _connection.getSQSConnector().sendMessages(destination, outgoingMessages);
            }
            _outgoingTxMessages.clear();
            _incomingStagedMessages.acknowledgeConsumedMessages();
        }
    }

    public void rollback() throws JMSException
    {
        checkClosed();
        if (_transacted)
        {
            _outgoingTxMessages.clear();
            _incomingStagedMessages.reset();
        }
    }

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

    public void recover() throws JMSException
    {
        checkClosed();
        if (_acknowledgeMode == CLIENT_ACKNOWLEDGE) {
            _incomingStagedMessages.reset();
        }
    }

    public MessageListener getMessageListener() throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public void setMessageListener(MessageListener messageListener) throws JMSException
    {
        checkClosed();
        // TODO
    }

    public void run()
    {
        // TODO
    }

    public NevadoMessageProducer createProducer(Destination destination) throws JMSException
    {
        checkClosed();
        NevadoMessageProducer producer = new NevadoMessageProducer(this, NevadoDestination.getInstance(destination));
        _producers.add(producer);
        return producer;
    }

    public NevadoMessageConsumer createConsumer(Destination destination) throws JMSException
    {
        return createConsumer(destination, null);
    }

    public NevadoMessageConsumer createConsumer(Destination destination, String selector) throws JMSException
    {
        return createConsumer(destination, selector, false);
    }

    public NevadoMessageConsumer createConsumer(Destination destination, String selector, boolean noLocal) throws JMSException
    {
        // TODO - Selector and noLocal currently ignored
        checkClosed();
        checkValidDestination(destination);
        NevadoMessageConsumer consumer = new NevadoMessageConsumer(this, NevadoDestination.getInstance(destination));
        _asyncConsumerRunner.addAsyncConsumer(consumer);
        _consumers.add(consumer);
        return consumer;
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public QueueBrowser createBrowser(Queue queue, String s) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public NevadoTemporaryQueue createTemporaryQueue() throws JMSException
    {
        checkClosed();
        return _connection.createTemporaryQueue();
    }

    public NevadoQueue createQueue(String s) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public NevadoTopic createTopic(String s) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException
    {
        checkClosed();
        return null; // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic, String s, boolean b) throws JMSException
    {
        checkClosed();
        return null; // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b) throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException
    {
        checkClosed();
        return null; // TODO
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException
    {
        checkClosed();
        return null;  // TODO
    }

    public void unsubscribe(String s) throws JMSException
    {
        checkClosed();
        // TODO
    }

    public void sendMessage(NevadoDestination destination, NevadoMessage message) throws JMSException
    {
        if (destination == null)
        {
            throw new NullPointerException("Destination is null");
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
            _outgoingTxMessages.get(destination).add(message.copyOf());
        }
        else
        {
            _connection.getSQSConnector().sendMessage(destination, message);
        }
    }

    public Message receiveMessage(NevadoDestination destination, long timeoutMs) throws JMSException
    {
        testBreak();
        NevadoMessage message = getUnfilteredMessage(destination, timeoutMs);

        // Filter expired messages
        while(message != null && message.getJMSExpiration() > 0
                && System.currentTimeMillis() > message.getJMSExpiration())
        {
            message.expire();
            _log.info("Skipped expired message (" + message.getJMSMessageID() + ")");

            message = getUnfilteredMessage(destination, timeoutMs);
        }

        // Auto-ack
        if (message != null && _acknowledgeMode == Session.AUTO_ACKNOWLEDGE)
        {
            message.acknowledge();
        }

        // Set session and destination
        return message;
    }

    private NevadoMessage getUnfilteredMessage(NevadoDestination destination, long timeoutMs) throws JMSException
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
        if (destination instanceof TemporaryQueue)
        {
            if (!_connection.ownsTemporaryQueue((TemporaryQueue)destination))
            {
                throw new InvalidDestinationException("Consumers for temporary queues cannot be created outside of " +
                        "connection where the temporary queue was created.");
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

    public String subscribe(NevadoTopic topic, NevadoQueue topicEndpoint) throws JMSException {
        return _connection.getSQSConnector().subscribe(topic, topicEndpoint);
    }
}
