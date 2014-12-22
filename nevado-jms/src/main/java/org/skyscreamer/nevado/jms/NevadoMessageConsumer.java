package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.*;
import javax.jms.IllegalStateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class NevadoMessageConsumer implements MessageConsumer, QueueReceiver, TopicSubscriber {
    private final Log _log = LogFactory.getLog(getClass());

    private boolean _closed = false;
    private final NevadoSession _session;
    private final NevadoDestination _destination;
    private final String _selector;
    private final boolean _noLocal;
    private volatile MessageListener _messageListener;
    private final AtomicReference<NevadoMessage> _messageParking = new AtomicReference<NevadoMessage>();

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

        //lazy start of AsyncConsumerRunner only when at least one MessageListener registered for consumer
        _session.getAsyncConsumerRunner().start();
    }

    @Override
    public NevadoMessage receive() throws JMSException {
        return receive(-1);
    }

    @Override
    public NevadoMessage receive(long timeoutMs) throws JMSException {
        checkClosed();
        checkAsync();
        NevadoMessage message;
        try {
            message = _session.receiveMessage(_destination, timeoutMs, _noLocal);
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
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
            List<JMSException> exceptions = new ArrayList<JMSException>();
            if (_destination instanceof NevadoTopic && !((NevadoTopic)_destination).isDurable())
            {
                try {
                    _session.getConnection().unsubscribe((NevadoTopic)_destination);
                } catch (JMSException e) {
                    _log.warn("Exception thrown trying to unsubscribe.  Will continue trying to close then will throw " +
                            "exception.  (First one if multiple.)", e);
                    exceptions.add(e);
                }
            }
            NevadoMessage parkedMessage;
            if ((parkedMessage = _messageParking.getAndSet(null)) != null)
            {
                _session.resetMessage(parkedMessage);
            }
            _messageListener = null;
            _closed = true;
            if (exceptions.size() > 0) {
                throw exceptions.get(0);
            }
        }
    }

    protected boolean processAsyncMessage() throws JMSException, InterruptedException {
        checkClosed();
        boolean messageProcessed = false;

        // First check for a parked message
        NevadoMessage message;
        if ((message = _messageParking.getAndSet(null)) == null)
        {
            message = _session.receiveMessage(_destination, 0, _noLocal);
        }
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
                    NevadoMessage parkedMessage;
                    if ((parkedMessage = _messageParking.getAndSet(message)) != null) {
                        _log.error("Stepped on an unexpected parked message.  Resetting it: " + parkedMessage);
                        _session.resetMessage(parkedMessage);
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
        return _noLocal;
    }
}
