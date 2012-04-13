package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.TestMessageListener;

import javax.jms.*;
import javax.jms.IllegalStateException;
import java.util.List;

/**
 * TODO - Description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageListenerTest extends AbstractJMSTest{
    @Test
    public void testMessageListener() throws JMSException, InterruptedException {
        TestMessageListener messageListener = new TestMessageListener();
        Session session = createSession();
        Queue tempQueue = createTempQueue();
        session.createConsumer(tempQueue).setMessageListener(messageListener);
        TextMessage testMessage1 = session.createTextMessage(RandomData.readString());
        TextMessage testMessage2 = session.createTextMessage(RandomData.readString());
        TextMessage testMessage3 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(testMessage1);
        Thread.sleep(500);
        producer.send(testMessage2);
        Thread.sleep(500);
        producer.send(testMessage3);
        Thread.sleep(2000);
        List<Message> messages = messageListener.getMessages();
        Assert.assertEquals(3, messages.size());
        Assert.assertTrue(messages.get(0) instanceof TextMessage);
        Assert.assertEquals(testMessage1.getText(), ((TextMessage) messages.get(0)).getText());
        Assert.assertEquals(testMessage2.getText(), ((TextMessage) messages.get(1)).getText());
        Assert.assertEquals(testMessage3.getText(), ((TextMessage) messages.get(2)).getText());
    }

    @Test(expected = IllegalStateException.class)
    public void testAsyncBlocksSync() throws JMSException {
        TestMessageListener messageListener = new TestMessageListener();
        Session session = createSession();
        Queue tempQueue = createTempQueue();
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        consumer.receiveNoWait();
    }

    @Test
    public void testAsyncThenSyncNoBlock() throws JMSException {
        TestMessageListener messageListener = new TestMessageListener();
        Session session = createSession();
        Queue tempQueue = createTempQueue();
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        consumer.setMessageListener(null);
        consumer.receiveNoWait();
    }
}
