package org.skyscreamer.nevado.jms.destination;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;
import java.util.Collection;
import java.util.UUID;

/**
 * Test temporary topics
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TemporaryTopicTest extends AbstractJMSTest {
    @Test
    public void testTemporaryTopic() throws Exception {
        NevadoSession session = createSession();
        TemporaryTopic temporaryTopic = session.createTemporaryTopic();
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(temporaryTopic);
        MessageConsumer consumer = session.createConsumer(temporaryTopic);
        producer.send(testMessage);
        Message msgOut = consumer.receive(5000);
        Assert.assertTrue(msgOut instanceof TextMessage);
        Assert.assertEquals("Message body not equal", testMessage.getText(), ((TextMessage) msgOut).getText());
    }

    @Test(expected = InvalidDestinationException.class)
    public void testTemporaryQueueAcrossConnections() throws Exception
    {
        NevadoSession session = createSession();
        TemporaryTopic temporaryTopic = session.createTemporaryTopic();
        Connection theWrongConnection = createConnection(getConnectionFactory());
        Session theWrongSession = theWrongConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        theWrongSession.createConsumer(temporaryTopic);
    }

    @Test
    public void testTemporaryTopicSuffix() throws Exception
    {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        String temporaryTopicSuffix = UUID.randomUUID().toString();
        Assert.assertTrue(temporaryTopicSuffix.length() > 0);
        connectionFactory.setTemporaryTopicSuffix(temporaryTopicSuffix);
        Connection connection = createConnection(connectionFactory);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryTopic topic = session.createTemporaryTopic();
        Assert.assertTrue(topic.getTopicName().endsWith(temporaryTopicSuffix));
        connection.close();
    }

    @Test
    public void testDeleteUnusedTemporaryTopics() throws Exception {
        NevadoConnection conn1;
        String suffix1;
        NevadoConnection conn2;
        String suffix2;

        {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            suffix1 = UUID.randomUUID().toString();
            connectionFactory.setTemporaryTopicSuffix(suffix1);
            conn1 = createConnection(connectionFactory);
        }
        {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            suffix2 = UUID.randomUUID().toString();
            connectionFactory.setTemporaryTopicSuffix(suffix2);
            conn2 = createConnection(connectionFactory);
        }

        try {
            conn1.start();
            conn2.start();
            NevadoTemporaryTopic topic1 = conn1.createSession(false, Session.AUTO_ACKNOWLEDGE).createTemporaryTopic();
            NevadoTemporaryTopic topic2 = conn2.createSession(false, Session.AUTO_ACKNOWLEDGE).createTemporaryTopic();
            Assert.assertTrue(conn1.listAllTemporaryTopics().contains(topic1));
            Assert.assertTrue(conn2.listAllTemporaryTopics().contains(topic2));
            conn1.deleteUnusedTemporaryTopics(suffix2 + "X");
            Assert.assertTrue(conn1.listAllTemporaryTopics().contains(topic1));
            Assert.assertTrue(conn2.listAllTemporaryTopics().contains(topic2));
            conn1.deleteUnusedTemporaryTopics(suffix2);
            Assert.assertTrue(conn1.listAllTemporaryTopics().contains(topic1));
            Assert.assertFalse(conn2.listAllTemporaryTopics().contains(topic2));
        }
        finally {
            try {
                conn1.close();
            }
            catch (Throwable t) {
                _log.error("Unable to close connection 1", t);
            }
            try {
                conn2.close();
            }
            catch (Throwable t) {
                _log.error("Unable to close connection 1", t);
            }
        }
    }

    // Because the queues returned by SNS ListTopics is not synchronous with creation and deletion of topics, it is
    // too flaky to test in a quick, automated fashion.  This could be done with thie very slow test
    // but we'll leave it disabled so our overall suite can remain fast.
    @Test
    public void testTemporaryTopicDeletion() throws Exception {
        NevadoSession session = createSession();
        TemporaryTopic temporaryTopic = session.createTemporaryTopic();
        Collection<NevadoTemporaryTopic> allTemporaryTopics = getConnection().listAllTemporaryTopics();
        Assert.assertTrue("Temporary topic should exist", allTemporaryTopics.contains(temporaryTopic));
        getConnection().close();
        allTemporaryTopics = getConnection().listAllTemporaryTopics();
        Assert.assertFalse("Temporary topic should not exist", allTemporaryTopics.contains(temporaryTopic));
    }
}
