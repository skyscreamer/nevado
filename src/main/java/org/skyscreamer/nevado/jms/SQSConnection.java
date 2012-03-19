package org.skyscreamer.nevado.jms;

import javax.jms.*;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/19/12
 * Time: 9:43 AM
 */
public class SQSConnection implements QueueConnection {

    public QueueSession createQueueSession(boolean b, int i) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectionConsumer createConnectionConsumer(Queue queue, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Session createSession(boolean b, int i) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getClientID() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setClientID(String s) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectionMetaData getMetaData() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ExceptionListener getExceptionListener() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void start() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stop() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
