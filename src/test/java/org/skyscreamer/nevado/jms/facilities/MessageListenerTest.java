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
        Session session = createSession();
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        consumer.receiveNoWait();
    }

    @Test
    public void testAsyncThenSyncNoBlock() throws JMSException {
        TestMessageListener messageListener = new TestMessageListener();
        Session session = createSession();
        Queue tempQueue = createTempQueue(session);
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        consumer.setMessageListener(null);
        consumer.receiveNoWait();
    }
}
