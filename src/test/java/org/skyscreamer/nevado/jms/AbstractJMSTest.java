package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryTopic;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.util.TestExceptionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

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
    protected final Log _log = LogFactory.getLog(AbstractJMSTest.class);

    private String _awsAccessKey;
    private String _awsSecretKey;

    @Autowired private ConnectionFactory _connectionFactory;
    private NevadoConnection _connection;
    private TestExceptionListener _exceptionListener = new TestExceptionListener();

    @Before
    public void setUp() throws JMSException, IOException {
        initializeAWSCredentials();
        _connection = createConnection(_connectionFactory);
        _connection.setExceptionListener(_exceptionListener);
        _connection.start();
    }

    protected NevadoConnection createConnection(ConnectionFactory connectionFactory) throws JMSException {
        return (NevadoConnection)connectionFactory.createConnection(_awsAccessKey, _awsSecretKey);
    }

    protected NevadoQueueConnection createQueueConnection(QueueConnectionFactory connectionFactory)
            throws JMSException
    {
        return (NevadoQueueConnection)connectionFactory.createQueueConnection(_awsAccessKey, _awsSecretKey);
    }

    protected NevadoTopicConnection createTopicConnection(TopicConnectionFactory connectionFactory)
            throws JMSException
    {
        return (NevadoTopicConnection)connectionFactory.createTopicConnection(_awsAccessKey, _awsSecretKey);
    }

    protected Message sendAndReceive(Message msg) throws JMSException {
        NevadoSession session = createSession();
        Queue testQueue = createTempQueue(session);
        session.createProducer(testQueue).send(msg);
        Message msgOut = createSession().createConsumer(testQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        return msgOut;
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
                System.out.println("ATTENTION: You have not set up your AWS credentials.  Follow the following steps:\n" +
                        "    1. Copy src/test/resources/aws.properties.TEMPLATE to src/test/resources/aws.properties\n" +
                        "    2. Edit aws.properties with your access keys from https://aws-portal.amazon.com/gp/aws/securityCredentials\n" +
                        "    3. Have git ignore the new file.  Add the following line to .git/info/exclude:\n" +
                        "        src/test/resources/aws.properties\n\n" +
                        "Be careful to keep your keys in a safe place and don't commit them to source control.");
            throw new MissingResourceException("Resource /aws.properties does not exist",
                    null, null);
        }
    }

    @After
    public void tearDown() throws JMSException {
        Assert.assertEquals("Exception listener caught some exceptions", 0, _exceptionListener.getExceptions().size());
        _connection.close();
    }

    public ConnectionFactory getConnectionFactory() {
        return _connectionFactory;
    }

    protected NevadoConnection getConnection() {
        return _connection;
    }

    protected NevadoSession createSession() throws JMSException
    {
        return (NevadoSession)_connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    protected NevadoTemporaryQueue createTempQueue(NevadoSession session) throws JMSException
    {
        return session.createTemporaryQueue();
    }

    protected NevadoTemporaryTopic createTempTopic(NevadoSession session) throws JMSException {
        return session.createTemporaryTopic();
    }

    protected void deleteQueue(NevadoQueue queue) throws JMSException
    {
        _connection.getSQSConnector().deleteQueue(queue);
    }

    protected void compareTextMessages(TextMessage[] expectedTextMessages, TextMessage[] actualTextMessages) throws JMSException {
        if (expectedTextMessages == null)
        {
            throw new NullPointerException("Expected text messages cannot be null");
        }
        junit.framework.Assert.assertNotNull("Actual text message array null", actualTextMessages);
        junit.framework.Assert.assertEquals("Expected text message array size does not equal actual", expectedTextMessages.length,
                actualTextMessages.length);
        Map<String, Integer> expectedTextCount = countTexts(expectedTextMessages);
        Map<String, Integer> actualTextCount = countTexts(expectedTextMessages);
        junit.framework.Assert.assertEquals("Compare expected and actual text messages", expectedTextCount, actualTextCount);
    }

    private Map<String, Integer> countTexts(TextMessage[] expectedTextMessages) throws JMSException {
        Map<String, Integer> textCount = new HashMap<String, Integer>();
        for(TextMessage textMessage : expectedTextMessages) {
            String text = textMessage.getText();
            int count = textCount.containsKey(text) ? textCount.get(text) : 0;
            ++count;
            textCount.put(text, count);
        }
        return textCount;
    }

    protected void breakSession(NevadoSession session)
    {
        session.setBreakSessionForTesting(true);
    }
}
