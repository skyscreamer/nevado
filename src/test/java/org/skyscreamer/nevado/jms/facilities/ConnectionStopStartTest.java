package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoSession;
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
        NevadoConnection conn = createConnection(getConnectionFactory());
        NevadoSession session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue tempQueue = createTempQueue(session);
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
        NevadoConnection conn = createConnection(getConnectionFactory());
        NevadoSession asyncSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TestMessageListener messageListener = new TestMessageListener();
        Queue tempQueue = createTempQueue(asyncSession);
        asyncSession.createConsumer(tempQueue).setMessageListener(messageListener);
        MessageProducer asyncProducer = asyncSession.createProducer(tempQueue);
        String asyncTestBody = RandomData.readString();
        TextMessage asyncTestMessage = asyncSession.createTextMessage(asyncTestBody);
        asyncProducer.send(asyncTestMessage);
        Thread.sleep(200);
        Assert.assertTrue(messageListener.isEmpty());

        conn.start();

        TextMessage message = (TextMessage)messageListener.getMessage(1000);
        Assert.assertEquals(asyncTestBody, message.getText());
    }
    
    @Test
    public void testClientPause() throws Exception {
        // Set up and send two messages
        NevadoConnection conn = getConnection();
        NevadoSession session = createSession();
        String testBody1 = RandomData.readString();
        String testBody2 = RandomData.readString();
        Queue tempQueue = createTempQueue(session);
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
        NevadoConnection conn = getConnection();
        NevadoSession session = createSession();
        TextMessage testMsg1 = session.createTextMessage(RandomData.readString());
        TextMessage testMsg2 = session.createTextMessage(RandomData.readString());
        Queue tempQueue = createTempQueue(session);
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(testMsg1);
        producer.send(testMsg2);

        // Add listener
        TestMessageListener messageListener = new TestMessageListener();
        session.createConsumer(tempQueue).setMessageListener(messageListener);
        TextMessage msgOut1 = (TextMessage)messageListener.getMessage(1000);
        TextMessage msgOut2 = (TextMessage)messageListener.getMessage(1000);
        compareTextMessages(new TextMessage[] { testMsg1, testMsg2}, new TextMessage[] { msgOut1, msgOut2 });
        Assert.assertTrue(messageListener.isEmpty());

        // Pause
        conn.stop();
        String testBody3 = RandomData.readString();
        producer.send(session.createTextMessage(testBody3));
        Thread.sleep(200);
        Assert.assertTrue(messageListener.isEmpty());
        conn.start();
        Assert.assertEquals(testBody3, ((TextMessage)messageListener.getMessage(1000)).getText());
        Assert.assertTrue(messageListener.isEmpty());
    }
}
