package org.skyscreamer.nevado.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/18/12
 * Time: 8:49 PM
 */
public class SQSConnectionFactory implements QueueConnectionFactory {
    private String awsAccessKey;
    private String awsSecretKey;

    public QueueConnection createQueueConnection() throws JMSException {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public QueueConnection createQueueConnection(String s, String s1) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Connection createConnection() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Connection createConnection(String s, String s1) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
