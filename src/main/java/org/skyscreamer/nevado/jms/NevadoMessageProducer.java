package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * Created by IntelliJ IDEA.
 * User: cpage
 * Date: 3/21/12
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class NevadoMessageProducer implements MessageProducer {
    private NevadoSession _session;
    private NevadoDestination _destination;
    private boolean _disableMessageID = false;
    private boolean _disableTimestamp = false;

    public NevadoMessageProducer(NevadoSession session, NevadoDestination destination) throws JMSException {
        _session = session;
        _destination = destination;
    }

    public void setDisableMessageID(boolean disableMessageID) throws JMSException {
        _disableMessageID = disableMessageID;
    }

    public boolean getDisableMessageID() throws JMSException {
        return _disableMessageID;
    }

    public void setDisableMessageTimestamp(boolean disableTimestamp) throws JMSException {
        _disableTimestamp = disableTimestamp;
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return _disableTimestamp;
    }

    public void setDeliveryMode(int i) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getDeliveryMode() throws JMSException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPriority(int i) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getPriority() throws JMSException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTimeToLive(long l) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getTimeToLive() throws JMSException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Destination getDestination() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(Message message) throws JMSException {
        NevadoMessage nevadoMessage = NevadoMessage.getInstance(message);
        _session.sendMessage(_destination, nevadoMessage, _disableMessageID, _disableTimestamp);
    }

    public void send(Message message, int i, int i1, long l) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(Destination destination, Message message) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(Destination destination, Message message, int i, int i1, long l) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
