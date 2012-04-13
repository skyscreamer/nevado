package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.TestMessageListener;

import javax.jms.*;

/**
 * Tests starting and stopping connections (JMS 1.1, Sec. 4.3.3 & 4.3.4)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionStopStartTest extends AbstractJMSTest {
    @Test
    public void testClientStart() throws Exception {
        // Set up session for sync messages
        Connection conn = createConnection(getConnectionFactory());
        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue tempQueue = createTempQueue();
        MessageProducer producer = session.createProducer(tempQueue);
        String testBody = RandomData.readString();
        TextMessage testMessage = session.createTextMessage(testBody);
        producer.send(testMessage);

        MessageConsumer consumer = session.createConsumer(tempQueue);
        Message msg = consumer.receive(500);
        Assert.assertNull(msg);

        conn.start();
        msg = consumer.receiveNoWait();
        msg.acknowledge();
        Assert.assertTrue(msg instanceof TextMessage);
        Assert.assertEquals(testBody, ((TextMessage)msg).getText());
    }

    @Test
    public void testAsyncClientStart() throws Exception {
        // Set up session for async messages
        Connection conn = createConnection(getConnectionFactory());
        Session asyncSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TestMessageListener messageListener = new TestMessageListener();
        Queue tempQueue = createTempQueue();
        asyncSession.createConsumer(tempQueue).setMessageListener(messageListener);
        MessageProducer asyncProducer = asyncSession.createProducer(tempQueue);
        String asyncTestBody = RandomData.readString();
        TextMessage asyncTestMessage = asyncSession.createTextMessage(asyncTestBody);
        asyncProducer.send(asyncTestMessage);
        Thread.sleep(100);
        Assert.assertEquals(0, messageListener.getMessages().size());

        conn.start();

        Thread.sleep(300);
        Assert.assertEquals(1, messageListener.getMessages().size());
        Assert.assertEquals(asyncTestBody, ((TextMessage)messageListener.getMessages().get(0)).getText());
    }
    
    @Test
    public void testClientPause() throws Exception {
        // Set up and send two messages
        Connection conn = getConnection();
        Session session = createSession();
        String testBody1 = RandomData.readString();
        String testBody2 = RandomData.readString();
        Queue tempQueue = createTempQueue();
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(session.createTextMessage(testBody1));
        producer.send(session.createTextMessage(testBody2));

        // Wait for the first message
        MessageConsumer consumer = session.createConsumer(tempQueue);
        TextMessage msg = (TextMessage)consumer.receive();
        msg.acknowledge();
        if (testBody1.equals(msg.getText()))
        {
            // OK
        }
        else if (testBody2.equals(msg.getText()))
        {
            // Order's mixed up.  Switch the two.
            String tmp = testBody1;
            testBody1 = testBody2;
            testBody2 = tmp;
        }
        else
        {
            Assert.fail("Message does not match either message sent");
        }

        // Pause and ensure the second message isn't coming
        conn.stop();
        Assert.assertNull(consumer.receive(500));

        // Restart and pick up second message
        conn.start();
        msg = (TextMessage)consumer.receiveNoWait();
        Assert.assertNotNull(msg);
        msg.acknowledge();
        Assert.assertTrue(msg instanceof TextMessage);
        Assert.assertEquals(testBody2, ((TextMessage) msg).getText());
    }

    @Test(timeout = 10000)
    public void testAsyncClientPause() throws Exception {
        // Set up listener
        Connection conn = getConnection();
        Session session = createSession();
        String testBody1 = RandomData.readString();
        String testBody2 = RandomData.readString();
        Queue tempQueue = createTempQueue();
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(session.createTextMessage(testBody1));
        producer.send(session.createTextMessage(testBody2));

        // Add listener
        TestMessageListener messageListener = new TestMessageListener();
        session.createConsumer(tempQueue).setMessageListener(messageListener);
        Thread.sleep(500);
        Assert.assertEquals(2, messageListener.getMessages().size());
        Assert.assertEquals(testBody1, ((TextMessage)messageListener.getMessages().get(0)).getText());
        Assert.assertEquals(testBody2, ((TextMessage)messageListener.getMessages().get(1)).getText());

        // Pause
        conn.stop();
        String testBody3 = RandomData.readString();
        producer.send(session.createTextMessage(testBody3));
        Thread.sleep(200);
        Assert.assertEquals(2, messageListener.getMessages().size());
        conn.start();
        Thread.sleep(1000);
        Assert.assertEquals(3, messageListener.getMessages().size());
        Assert.assertEquals(testBody3, ((TextMessage)messageListener.getMessages().get(2)).getText());
    }
}
