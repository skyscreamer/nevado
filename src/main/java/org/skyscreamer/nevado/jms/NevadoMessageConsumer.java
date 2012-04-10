package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;

import javax.jms.*;
import javax.jms.IllegalStateException;

public class NevadoMessageConsumer implements MessageConsumer {
    private final NevadoSession _session;
    private final NevadoDestination _destination;
    private volatile MessageListener _messageListener;

    public NevadoMessageConsumer(NevadoSession session, NevadoDestination destination) throws JMSException {
        _session = session;
        _destination = destination;
    }

    public String getMessageSelector() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public synchronized MessageListener getMessageListener() {
        return _messageListener;
    }

    public synchronized void setMessageListener(MessageListener messageListener) throws JMSException {
        _messageListener = messageListener;
    }

    public synchronized Message receive() throws JMSException {
        checkAsync();
        return _session.receiveMessage(_destination, -1);
    }

    public synchronized Message receive(long timeoutMs) throws JMSException {
        checkAsync();
        return _session.receiveMessage(_destination, timeoutMs);
    }

    public synchronized Message receiveNoWait() throws JMSException {
        checkAsync();
        return _session.receiveMessage(_destination, 0);
    }

    public synchronized void close() throws JMSException {
        _messageListener = null;
    }

    protected synchronized boolean processAsyncMessage() throws JMSException {
        boolean messageProcessed = false;
        Message message = _session.receiveMessage(_destination, 0);
        if (message != null) {
            getMessageListener().onMessage(message);
            messageProcessed = true;
        }
        return messageProcessed;
    }

    private void checkAsync() throws IllegalStateException {
        if (_messageListener != null)
        {
            throw new IllegalStateException("Synchronous message delivery cannot be requested from a consumer after " +
                    "a message listener has been registered");
        }
    }

    protected Destination getDestination() {
        return _destination;
    }
}
