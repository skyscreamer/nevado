package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.TestMessageListener;
import org.skyscreamer.nevado.jms.util.TestMessageListenerRuntimeException;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Tests Nevado implementation of MessageListener
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageListenerTest extends AbstractJMSTest {
    @Test
    public void testMessageListener() throws JMSException, InterruptedException {
        TestMessageListener messageListener = new TestMessageListener();
        NevadoSession session = createSession();
        Queue tempQueue = createTempQueue(session);
        session.createConsumer(tempQueue).setMessageListener(messageListener);
        TextMessage testMessage1 = session.createTextMessage(RandomData.readString());
        TextMessage testMessage2 = session.createTextMessage(RandomData.readString());
        TextMessage testMessage3 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(testMessage1);
        producer.send(testMessage2);
        producer.send(testMessage3);
        TextMessage msgOut1 = (TextMessage)messageListener.getMessage(1000);
        TextMessage msgOut2 = (TextMessage)messageListener.getMessage(1000);
        TextMessage msgOut3 = (TextMessage)messageListener.getMessage(1000);
        compareTextMessages(new TextMessage[] {testMessage1, testMessage2, testMessage3},
                new TextMessage[] {msgOut1, msgOut2, msgOut3});
        Assert.assertTrue(messageListener.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void testAsyncBlocksSync() throws JMSException {
        TestMessageListener messageListener = new TestMessageListener();
        NevadoSession session = createSession();
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        consumer.receiveNoWait();
    }

    @Test
    public void testAsyncThenSyncNoBlock() throws JMSException {
        TestMessageListener messageListener = new TestMessageListener();
        NevadoSession session = createSession();
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        consumer.setMessageListener(null);
        consumer.receiveNoWait();
    }

//    The result of a listener throwing a RuntimeException depends on the session's acknowledgement mode.
//
//    AUTO_ACKNOWLEDGEMENT or DUPS_OK_ACKNOWLEDGE - the message will be immediately redelivered. The number of attempts is provider-dependent. The JMSRedelivered header will be set.
//    CLIENT_ACKNOWLEDGE - The next message for the listener is delivered. In this case the client must manually recover.
//    Transacted Session - The next message for the listener is delivered. RuntimeException does not automatically rollback the session, and the client must do it explicitly.

    @Test
    public void testThrowRuntimeAutoAck() throws JMSException, InterruptedException {
        TestMessageListenerRuntimeException messageListener = new TestMessageListenerRuntimeException();
        NevadoConnection connection = createConnection(getConnectionFactory());
        connection.start();
        NevadoSession session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(msg1);
        producer.send(msg2);
        Message msgOut = messageListener.getMessage(5000);
        if (msg1.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg1, msgOut);
        else if (msg2.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg2, msgOut);
        else
            Assert.fail("Message did not match either sent: " + msgOut);
    }

    @Test
    public void testThrowRuntimeDupsOk() throws JMSException, InterruptedException {
        TestMessageListenerRuntimeException messageListener = new TestMessageListenerRuntimeException();
        NevadoConnection connection = createConnection(getConnectionFactory());
        connection.start();
        NevadoSession session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(msg1);
        producer.send(msg2);
        Message msgOut = messageListener.getMessage(5000);
        if (msg1.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg1, msgOut);
        else if (msg2.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg2, msgOut);
        else
            Assert.fail("Message did not match either sent: " + msgOut);
    }

    @Test
    public void testThrowRuntimeClientAck() throws JMSException, InterruptedException {
        TestMessageListenerRuntimeException messageListener = new TestMessageListenerRuntimeException();
        NevadoConnection connection = createConnection(getConnectionFactory());
        connection.start();
        NevadoSession session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(msg1);
        producer.send(msg2);
        Message msgOut = messageListener.getMessage(5000);
        if (msg1.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg2, msgOut);
        else if (msg2.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg1, msgOut);
        else
            Assert.fail("Message did not match either sent: " + msgOut);
    }

    @Test
    public void testThrowRuntimeTransacted() throws JMSException, InterruptedException {
        TestMessageListenerRuntimeException messageListener = new TestMessageListenerRuntimeException();
        NevadoConnection connection = createConnection(getConnectionFactory());
        connection.start();
        NevadoSession session = connection.createSession(true, Session.SESSION_TRANSACTED);
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(msg1);
        session.commit();
        producer.send(msg2);
        session.commit();
        Message msgOut = messageListener.getMessage(5000);
        if (msg1.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg2, msgOut);
        else if (msg2.equals(messageListener.getFirstMessage()))
            Assert.assertEquals(msg1, msgOut);
        else
            Assert.fail("Message did not match either sent: " + msgOut);
    }
}
