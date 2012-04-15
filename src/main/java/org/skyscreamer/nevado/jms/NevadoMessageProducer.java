package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Created by IntelliJ IDEA.
 * User: cpage
 * Date: 3/21/12
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class NevadoMessageProducer implements MessageProducer {
    private boolean _closed = false;
    private NevadoSession _session;
    private NevadoDestination _destination;
    private boolean _disableMessageID = false;
    private boolean _disableTimestamp = false;

    public NevadoMessageProducer(NevadoSession session, NevadoDestination destination) throws JMSException {
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

    public void setDeliveryMode(int i) throws JMSException {
        checkClosed();
        // TODO
    }

    public int getDeliveryMode() throws JMSException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPriority(int i) throws JMSException {
        checkClosed();
        // TODO
    }

    public int getPriority() throws JMSException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTimeToLive(long l) throws JMSException {
        checkClosed();
        // TODO
    }

    public long getTimeToLive() throws JMSException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Destination getDestination() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    public void send(Destination destination, Message message) throws JMSException {
        send(destination, message, Message.DEFAULT_DELIVERY_MODE, Message.DEFAULT_PRIORITY,
                Message.DEFAULT_TIME_TO_LIVE);
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

    private void checkClosed() throws javax.jms.IllegalStateException
    {
        if (_closed)
        {
            throw new IllegalStateException("This producer has been closed");
        }
    }
}
