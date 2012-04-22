package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.TestMessageListener;

import javax.jms.*;
import javax.jms.IllegalStateException;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests transactional behavior for sessions, per JMS 1.1 Sec. 4.4.7
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class SessionTransactionTest extends AbstractJMSTest {
    @Test
    public void testTransaction() throws JMSException, InterruptedException {
        Session controlSession = createSession();

        // Create a couple of temporary queues for the test
        Queue testConsumeQueue = controlSession.createTemporaryQueue();
        Queue testProduceQueue = controlSession.createTemporaryQueue();

        // Put some messages in a queue and set up the listener to monitor production
        TextMessage ctlMsg1 = controlSession.createTextMessage(RandomData.readString());
        TextMessage ctlMsg2 = controlSession.createTextMessage(RandomData.readString());
        TextMessage ctlMsg3 = controlSession.createTextMessage(RandomData.readString());
        MessageProducer producer = controlSession.createProducer(testConsumeQueue);
        producer.send(ctlMsg1);
        producer.send(ctlMsg2);
        producer.send(ctlMsg3);
        MessageConsumer consumer = controlSession.createConsumer(testProduceQueue);

        // Read some messages, send some messages
        Session txSession = getConnection().createSession(true, Session.SESSION_TRANSACTED);
        MessageConsumer txConsumer = txSession.createConsumer(testConsumeQueue);
        TextMessage msg1 = (NevadoTextMessage) txConsumer.receive();
        TextMessage msg2 = (NevadoTextMessage) txConsumer.receive();
        TextMessage msg3 = (NevadoTextMessage) txConsumer.receive();
        compareTextMessages(new TextMessage[] {ctlMsg1, ctlMsg2, ctlMsg3}, new TextMessage[] {msg1, msg2, msg3});
        Assert.assertNull(txConsumer.receive(100));
        MessageProducer txProducer = txSession.createProducer(testProduceQueue);
        TextMessage rollbackMsg1 = txSession.createTextMessage(RandomData.readString());
        TextMessage rollbackMsg2 = txSession.createTextMessage(RandomData.readString());
        _log.info("These messages are going to be rolled back, so should never be seen again: "
            + rollbackMsg1 + " " + rollbackMsg2);
        txProducer.send(rollbackMsg1);
        txProducer.send(rollbackMsg2);

        // Test that nothing has been sent yet
        Assert.assertNull("Messages sent in a transaction were transmitted before they were committed", consumer.receive(100));

        // Rollback, re-read and re-send
        txSession.rollback();
        msg1 = (NevadoTextMessage) txConsumer.receive();
        msg2 = (NevadoTextMessage) txConsumer.receive();
        msg3 = (NevadoTextMessage) txConsumer.receive();
        compareTextMessages(new TextMessage[] {ctlMsg1, ctlMsg2, ctlMsg3}, new TextMessage[] {msg1, msg2, msg3});
        Assert.assertNull(txConsumer.receive(100));
        TextMessage commitMsg1 = txSession.createTextMessage(RandomData.readString());
        TextMessage commitMsg2 = txSession.createTextMessage(RandomData.readString());
        txProducer.send(commitMsg1);
        txProducer.send(commitMsg2);

        // Test that nothing has been sent yet
        Assert.assertNull(consumer.receive(100));

        // Commit and check the results
        txSession.commit();
        Thread.sleep(100);
        Assert.assertNull(txConsumer.receiveNoWait());
        TextMessage msgOut1 = (TextMessage)consumer.receiveNoWait();
        TextMessage msgOut2 = (TextMessage)consumer.receiveNoWait();
        Assert.assertNull(consumer.receiveNoWait());
        compareTextMessages(new TextMessage[] {commitMsg1, commitMsg2}, new TextMessage[] {msgOut1, msgOut2});
    }

    @Test
    public void testTransactionRollbackPartialReplay() throws JMSException, InterruptedException {
        Session controlSession = createSession();

        // Create a couple of temporary queues for the test
        Queue testConsumeQueue = controlSession.createTemporaryQueue();

        // Put some messages in a queue and set up the listener to monitor production
        TextMessage ctlMsg1 = controlSession.createTextMessage(RandomData.readString());
        TextMessage ctlMsg2 = controlSession.createTextMessage(RandomData.readString());
        TextMessage ctlMsg3 = controlSession.createTextMessage(RandomData.readString());
        MessageProducer producer = controlSession.createProducer(testConsumeQueue);
        producer.send(ctlMsg1);
        producer.send(ctlMsg2);
        producer.send(ctlMsg3);

        // Read some messages, send some messages
        Session txSession = getConnection().createSession(true, Session.SESSION_TRANSACTED);
        MessageConsumer txConsumer = txSession.createConsumer(testConsumeQueue);
        TextMessage msg1 = (NevadoTextMessage) txConsumer.receive();
        TextMessage msg2 = (NevadoTextMessage) txConsumer.receive();
        TextMessage msg3 = (NevadoTextMessage) txConsumer.receive();
        compareTextMessages(new TextMessage[] {ctlMsg1, ctlMsg2, ctlMsg3}, new TextMessage[] {msg1, msg2, msg3});
        Assert.assertNull(txConsumer.receive(100));

        // Rollback, re-read (partially) and re-send
        txSession.rollback();
        TextMessage rollbackMsg1 = (NevadoTextMessage) txConsumer.receive();
        TextMessage rollbackMsg2 = (NevadoTextMessage) txConsumer.receive();
        compareTextMessages(new TextMessage[] {msg1, msg2}, new TextMessage[] {rollbackMsg1, rollbackMsg2});

        // Commit and check the results
        txSession.commit();
        TextMessage rollbackMsg3 = (NevadoTextMessage) txConsumer.receive(500);
        Assert.assertEquals(msg3.getText(), rollbackMsg3.getText());
        txSession.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testCommitNoTx() throws JMSException
    {
        NevadoSession session = createSession();
        session.commit();
    }
}
