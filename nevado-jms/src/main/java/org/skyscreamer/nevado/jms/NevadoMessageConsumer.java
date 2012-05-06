package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.destination.*;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoProperty;

import javax.jms.*;
import javax.jms.IllegalStateException;

public class NevadoMessageConsumer implements MessageConsumer, QueueReceiver, TopicSubscriber {
    private final Log _log = LogFactory.getLog(getClass());

    private boolean _closed = false;
    private final NevadoSession _session;
    private final NevadoDestination _destination;
    private final String _selector;
    private final boolean _noLocal;
    private volatile MessageListener _messageListener;

    public NevadoMessageConsumer(NevadoSession session, NevadoDestination destination, String selector, boolean noLocal)
            throws JMSException
    {
        _session = session;
        if (destination instanceof NevadoTopic)
        {
            NevadoTemporaryQueue topicEndpoint = _session.getConnection().createTemporaryQueue();
            String subscriptionArn = _session.getConnection().subscribe((NevadoTopic)destination, topicEndpoint);
            _destination = new NevadoTopic((NevadoTopic)destination, topicEndpoint, subscriptionArn, false);
        }
        else
        {
            _destination = destination;
        }
        _selector = selector;
        _noLocal = noLocal;
    }

    public NevadoMessageConsumer(NevadoSession session, NevadoTopic topic, String durableSubscriptionName,
                                 String selector, boolean noLocal)
            throws JMSException
    {
        _session = session;
        NevadoQueue topicEndpoint
                = _session.createInternalQueue(_session.getDurableEndpointQueueName(durableSubscriptionName));
        String subscriptionArn = _session.getConnection().subscribe(topic, topicEndpoint);
        _destination = new NevadoTopic(topic, topicEndpoint, subscriptionArn, true);
        _selector = selector;
        _noLocal = noLocal;
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

    @Override
    public NevadoMessage receive() throws JMSException {
        return receive(-1);
    }

    @Override
    public NevadoMessage receive(long timeoutMs) throws JMSException {
        checkClosed();
        checkAsync();
        NevadoMessage message = _session.receiveMessage(_destination, timeoutMs, _noLocal);
        tryAutoAck(message);
        return message;
    }

    @Override
    public NevadoMessage receiveNoWait() throws JMSException {
        return receive(0);
    }

    public synchronized void close() throws JMSException {
        if (!_closed)
        {
            if (_destination instanceof NevadoTopic && !((NevadoTopic)_destination).isDurable())
            {
                _session.getConnection().unsubscribe((NevadoTopic)_destination);
            }
            _messageListener = null;
            _closed = true;
        }
    }

    protected boolean processAsyncMessage() throws JMSException
    {
        checkClosed();
        boolean messageProcessed = false;
        NevadoMessage message = _session.receiveMessage(_destination, 0, _noLocal);
        if (message != null) {
            try {
                getMessageListener().onMessage(message);
                tryAutoAck(message);
                messageProcessed = true;
            }
            catch(Throwable t) {
                if (_session.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE
                        || _session.getAcknowledgeMode() == Session.DUPS_OK_ACKNOWLEDGE)
                {
                    _session.resetMessage(message);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        _log.warn(e);
                    }
                }
            }
        }
        return messageProcessed;
    }

    private void tryAutoAck(NevadoMessage message) throws JMSException {
        if (message != null && _session.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE)
        {
            message.acknowledge();
        }
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
