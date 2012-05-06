package org.skyscreamer.nevado.jms.destination;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;
import java.util.Collection;

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
        Message msgOut = consumer.receive();
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

    // Because the queues returned by SNS ListTopics is not synchronous with creation and deletion of topics, it is
    // too flaky to test in a quick, automated fashion.  This could be done with thie very slow test
    // but we'll leave it disabled so our overall suite can remain fast.
    //@Test
    public void testTemporaryTopicDeletion() throws Exception {
        NevadoSession session = createSession();
        TemporaryTopic temporaryTopic = session.createTemporaryTopic();
        Thread.sleep(15000);
        Collection<TemporaryTopic> allTemporaryTopics = getConnection().listAllTemporaryTopics();
        Assert.assertTrue("Temporary topic should exist", allTemporaryTopics.contains(temporaryTopic));
        getConnection().close();
        Thread.sleep(60000);
        allTemporaryTopics = getConnection().listAllTemporaryTopics();
        Assert.assertFalse("Temporary topic should not exist", allTemporaryTopics.contains(temporaryTopic));
    }
}
