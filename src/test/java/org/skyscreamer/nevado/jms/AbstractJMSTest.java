package org.skyscreamer.nevado.jms;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/22/12
 * Time: 3:23 AM
 */
public abstract class AbstractJMSTest {
    private static final String TEST_QUEUE_NAME = "testQueue";

    @Autowired private ConnectionFactory connectionFactory;
    private Session _session;
    private Queue _testQueue = new NevadoQueue(TEST_QUEUE_NAME);

    @Before
    protected void setUp() throws JMSException {
        _session = connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
        createPhysicalQueue(TEST_QUEUE_NAME);
    }

    @After
    protected void tearDown() throws JMSException {
        removePhysicalQueue(TEST_QUEUE_NAME);
    }

    private void createPhysicalQueue(String queueName) {
        // TODO
    }

    private void removePhysicalQueue(String queueName) {
        // TODO
    }

    protected Session getSession() {
        return _session;
    }
    
    protected Queue getTestQueue() {
        return _testQueue;
    }
}
