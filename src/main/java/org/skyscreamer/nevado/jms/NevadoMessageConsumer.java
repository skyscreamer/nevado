package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

public class NevadoMessageConsumer implements MessageConsumer {
    private NevadoSession _session;
    private NevadoDestination _destination;

    public NevadoMessageConsumer(NevadoSession session, NevadoDestination destination) throws JMSException {
        _session = session;
        _destination = destination;
    }

    public String getMessageSelector() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MessageListener getMessageListener() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setMessageListener(MessageListener messageListener) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Message receive() throws JMSException {
        return _session.receiveMessage(_destination, -1);
    }

    public Message receive(long timeoutMs) throws JMSException {
        return _session.receiveMessage(_destination, timeoutMs);
    }

    public Message receiveNoWait() throws JMSException {
        return _session.receiveMessage(_destination, 0);
    }

    public void close() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
