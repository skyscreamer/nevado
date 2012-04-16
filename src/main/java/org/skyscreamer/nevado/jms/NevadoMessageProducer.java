package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Nevado implementation of a MessageProducer.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoMessageProducer implements MessageProducer, QueueSender {
    private boolean _closed = false;
    private final NevadoSession _session;
    private final NevadoDestination _destination;
    private boolean _disableMessageID = false;
    private boolean _disableTimestamp = false;
    private int _deliveryMode = Message.DEFAULT_DELIVERY_MODE;
    private int _priority = Message.DEFAULT_PRIORITY;
    private long _ttl = Message.DEFAULT_TIME_TO_LIVE;

    public NevadoMessageProducer(NevadoSession session, NevadoDestination destination) throws JMSException {
        if (session == null || destination == null) {
            throw new NullPointerException();
        }
        _session = session;
        _destination = destination;
    }

    public void setDisableMessageID(boolean disableMessageID) throws JMSException {
        checkClosed();
        _disableMessageID = disableMessageID;
    }

    public boolean getDisableMessageID() throws JMSException {
        return _disableMessageID;
    }

    public void setDisableMessageTimestamp(boolean disableTimestamp) throws JMSException {
        checkClosed();
        _disableTimestamp = disableTimestamp;
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return _disableTimestamp;
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException {
        checkClosed();
        _deliveryMode = deliveryMode;
    }

    public int getDeliveryMode() {
        return _deliveryMode;
    }

    public void setPriority(int priority) throws JMSException {
        checkClosed();
        _priority = priority;
    }

    public int getPriority() {
        return _priority;
    }

    public void setTimeToLive(long ttl) throws JMSException {
        checkClosed();
        _ttl = ttl;
    }

    public long getTimeToLive() {
        return _ttl;
    }

    public Destination getDestination() throws JMSException {
        return _destination;
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

    public synchronized void close() throws JMSException {
        if (!_closed)
        {
            _closed = true;
        }
    }

    public void send(Message message) throws JMSException {
        send(_destination, message);
    }

    public void send(Message message, int deliveryMode, int priority, long ttl) throws JMSException {
        send(_destination, message, deliveryMode, priority, ttl);
    }

    public void send(Queue queue, Message message) throws JMSException {
        send(queue, message, _deliveryMode, _priority, _ttl);
    }

    public void send(Destination destination, Message message) throws JMSException {
        send(destination, message, _deliveryMode, _priority, _ttl);
    }

    public void send(Queue queue, Message message, int deliveryMode, int priority, long ttl)
            throws JMSException
    {
        if (!(_destination instanceof Queue))
        {
            throw new IllegalStateException("Only a QueueSender can send messages to a queue");
        }
        send((Destination)queue, message, deliveryMode, priority, ttl);
    }

    public void send(Destination destination, Message message, int deliveryMode, int priority, long ttl)
            throws JMSException
    {
        checkClosed();
        NevadoDestination nevadoDestination = NevadoDestination.getInstance(destination);
        NevadoMessage nevadoMessage = NevadoMessage.getInstance(message);
        nevadoMessage.setJMSDestination(destination);
        nevadoMessage.setJMSDeliveryMode(deliveryMode);
        nevadoMessage.setJMSPriority(priority);
        nevadoMessage.setJMSExpiration(ttl > 0 ? System.currentTimeMillis() + ttl : 0);
        nevadoMessage.setDisableMessageID(_disableMessageID);
        nevadoMessage.setDisableTimestamp(_disableTimestamp);
        _session.sendMessage(nevadoDestination, nevadoMessage);
    }

    public boolean isClosed()
    {
        return _closed;
    }

    private void checkClosed() throws IllegalStateException
    {
        if (_closed)
        {
            throw new IllegalStateException("This producer has been closed");
        }
    }
}
