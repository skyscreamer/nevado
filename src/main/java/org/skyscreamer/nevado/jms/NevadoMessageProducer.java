package org.skyscreamer.nevado.jms;

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
    public void setDisableMessageID(boolean b) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getDisableMessageID() throws JMSException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDisableMessageTimestamp(boolean b) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
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
