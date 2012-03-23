package org.skyscreamer.nevado.jms;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

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
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:/testContext.xml" })
public abstract class AbstractJMSTest {
    private static final String TEST_QUEUE_NAME = "testQueue";

    @Autowired private ConnectionFactory connectionFactory;
    private Session _session;
    private Queue _testQueue = new NevadoQueue(TEST_QUEUE_NAME);

    @Before
    public void setUp() throws JMSException {
        _session = connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
        createPhysicalQueue(TEST_QUEUE_NAME);
    }

    @After
    public void tearDown() throws JMSException {
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
