package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;

import javax.jms.*;
import javax.jms.IllegalStateException;

public class NevadoMessageConsumer implements MessageConsumer, QueueReceiver, TopicSubscriber {
    private boolean _closed = false;
    private final NevadoSession _session;
    private final NevadoDestination _destination;
    private volatile MessageListener _messageListener;

    public NevadoMessageConsumer(NevadoSession session, NevadoDestination destination) throws JMSException {
        _session = session;
        if (destination instanceof NevadoTopic)
        {
            NevadoTemporaryQueue topicEndpoint = _session.getConnection().createTemporaryQueue();
            String subscriptionArn = _session.subscribe((NevadoTopic)destination, topicEndpoint);
            _destination = new NevadoTopic((NevadoTopic)destination, topicEndpoint, subscriptionArn);
        }
        else
        {
            _destination = destination;
        }
    }

    public String getMessageSelector() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MessageListener getMessageListener() {
        return _messageListener;
    }

    public void setMessageListener(MessageListener messageListener) throws JMSException {
        checkClosed();
        _messageListener = messageListener;
    }

    public Message receive() throws JMSException {
        checkClosed();
        checkAsync();
        return _session.receiveMessage(_destination, -1);
    }

    public Message receive(long timeoutMs) throws JMSException {
        checkClosed();
        checkAsync();
        return _session.receiveMessage(_destination, timeoutMs);
    }

    public Message receiveNoWait() throws JMSException {
        checkClosed();
        checkAsync();
        return _session.receiveMessage(_destination, 0);
    }

    public synchronized void close() throws JMSException {
        if (!_closed)
        {
            // TODO - unsubscribe _subscriptionArn
            _messageListener = null;
            _closed = true;
        }
    }

    protected boolean processAsyncMessage() throws JMSException
    {
        checkClosed();
        boolean messageProcessed = false;
        Message message = _session.receiveMessage(_destination, 0);
        if (message != null) {
            getMessageListener().onMessage(message);
            messageProcessed = true;
            if (_session.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE)
            {
                message.acknowledge();
            }
        }
        return messageProcessed;
    }

    private void checkAsync() throws IllegalStateException
    {
        if (_messageListener != null)
        {
            throw new IllegalStateException("Synchronous message delivery cannot be requested from a consumer after " +
                    "a message listener has been registered");
        }
    }

    public Queue getQueue() throws JMSException {
        if (_destination instanceof Queue)
        {
            return (Queue)_destination;
        }
        else
        {
            throw new IllegalStateException("getQueue() can only be called for a QueueSender");
        }
    }

    protected Destination getDestination()
    {
        return _destination;
    }

    public boolean isClosed()
    {
        return _closed;
    }

    private void checkClosed() throws IllegalStateException
    {
        if (_closed)
        {
            throw new IllegalStateException("This consumer has been closed");
        }
    }

    public Topic getTopic() throws JMSException {
        if (_destination instanceof Topic)
        {
            return (Topic)_destination;
        }
        else
        {
            throw new IllegalStateException("getTopic() can only be called for a TopicSubscriber");
        }
    }

    public boolean getNoLocal() throws JMSException {
        return false;  // TODO
    }
}
