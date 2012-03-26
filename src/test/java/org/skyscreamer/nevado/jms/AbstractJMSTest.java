package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import javax.jms.*;
import javax.jms.Queue;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    private final Log _log = LogFactory.getLog(AbstractJMSTest.class);

    private String _awsAccessKey;
    private String _awsSecretKey;

    @Autowired private ConnectionFactory connectionFactory;
    private Session _session;
    private Queue _testQueue = new NevadoQueue(TEST_QUEUE_NAME);

    @Before
    public void setUp() throws JMSException, IOException {
        initializeAWSCredentials();
        _session = connectionFactory.createConnection(_awsAccessKey, _awsSecretKey).createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        // Clear out the test queue
        int msgCount = 0;
        MessageConsumer consumer = _session.createConsumer(new NevadoQueue(TEST_QUEUE_NAME));
        Message message;
        while((message = consumer.receiveNoWait()) != null) {
            ++msgCount;
            message.acknowledge();
        }
        _log.info("Cleared out " + msgCount + " messages");
    }

    private void initializeAWSCredentials() throws IOException {
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("/aws.properties");
        prop.load(in);
        in.close();

        _awsAccessKey = prop.getProperty("aws.accessKey");
        _awsSecretKey = prop.getProperty("aws.secretKey");
        if (_awsAccessKey == null || _awsAccessKey.trim().length() == 0
            || _awsSecretKey == null || _awsSecretKey.trim().length() == 0) {
            throw new MissingResourceException("To run the tests, you need to set aws.accessKey and aws.secretKey " +
                    "in src/main/test/resources/aws.properties.  (But make sure not to commit or share that file!)",
                    null, null);
        }
    }

    @After
    public void tearDown() throws JMSException {
        // Do nothing
    }

    protected Session getSession() {
        return _session;
    }
    
    protected Queue getTestQueue() {
        return _testQueue;
    }
    
    public int getRandomInt() {
        return (new Random()).nextInt();
    }
    
    public String getRandomString() {
        return UUID.randomUUID().toString();
    }
}
