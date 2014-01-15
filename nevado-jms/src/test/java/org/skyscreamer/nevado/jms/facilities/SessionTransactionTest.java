package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;
import org.skyscreamer.nevado.jms.message.TestBrokenMessage;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;
import javax.jms.IllegalStateException;

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
        Queue queue1 = controlSession.createTemporaryQueue();
        Queue queue2 = controlSession.createTemporaryQueue();

        // Put some messages in a queue
        TextMessage ctlMsg1 = controlSession.createTextMessage(RandomData.readString());
        TextMessage ctlMsg2 = controlSession.createTextMessage(RandomData.readString());
        TextMessage ctlMsg3 = controlSession.createTextMessage(RandomData.readString());

        // Send some messages non-transactionally to queue #1
        MessageProducer nonTxProducer = controlSession.createProducer(queue1);
        nonTxProducer.send(ctlMsg1);
        nonTxProducer.send(ctlMsg2);
        nonTxProducer.send(ctlMsg3);

        // Read some messages with a transactional session from queue #1
        Session txSession = getConnection().createSession(true, Session.SESSION_TRANSACTED);
        MessageConsumer txConsumer = txSession.createConsumer(queue1);
        TextMessage msg1 = (NevadoTextMessage) txConsumer.receive();
        TextMessage msg2 = (NevadoTextMessage) txConsumer.receive();
        TextMessage msg3 = (NevadoTextMessage) txConsumer.receive();
        compareTextMessages(new TextMessage[] {ctlMsg1, ctlMsg2, ctlMsg3}, new TextMessage[] {msg1, msg2, msg3});
        Assert.assertNull(txConsumer.receive(100));

        // Send some messages transactionally to queue #2, don't commit, rollback
        MessageProducer txProducer = txSession.createProducer(queue2);
        TextMessage rollbackMsg1 = txSession.createTextMessage(RandomData.readString());
        TextMessage rollbackMsg2 = txSession.createTextMessage(RandomData.readString());
        _log.info("These messages are going to be rolled back, so should never be seen again: "
            + rollbackMsg1 + " " + rollbackMsg2);
        txProducer.send(rollbackMsg1);
        txProducer.send(rollbackMsg2);

        // Read message with non-transactional consumer
        MessageConsumer nonTxConsumer = controlSession.createConsumer(queue2);
        Assert.assertNull("Messages sent in a transaction were transmitted before they were committed",
                nonTxConsumer.receive(100));

        // Rollback transaction.  This should rollback the messages we read, and the messages we sent.
        txSession.rollback();

        // Re-receive the messages produces non-transactionally, and received transactionally (but rolled back)
        msg1 = (NevadoTextMessage) txConsumer.receive();
        msg2 = (NevadoTextMessage) txConsumer.receive();
        msg3 = (NevadoTextMessage) txConsumer.receive();
        compareTextMessages(new TextMessage[] {ctlMsg1, ctlMsg2, ctlMsg3}, new TextMessage[] {msg1, msg2, msg3});
        Assert.assertNull(txConsumer.receive(100));

        // Re-send transactionally sent messages
        TextMessage commitMsg1 = txSession.createTextMessage(RandomData.readString());
        TextMessage commitMsg2 = txSession.createTextMessage(RandomData.readString());
        txProducer.send(commitMsg1);
        _log.info("Sent message without committing: " + commitMsg1);
        txProducer.send(commitMsg2);
        _log.info("Sent message without committing: " + commitMsg2);

        // Test that nothing has been sent yet
        Message msg = nonTxConsumer.receive(500);
        Assert.assertNull("We still haven't committed.  Should be no messages yet.", msg);

        // Commit.  This should send the messages.  Check the results.  Give it a little time for async connectors.
        txSession.commit();
        Assert.assertNull(txConsumer.receive(100));
        TextMessage msgOut1 = (TextMessage)nonTxConsumer.receive(1000);
        _log.info("Got msg: " + msgOut1);
        TextMessage msgOut2 = (TextMessage)nonTxConsumer.receive(1000);
        _log.info("Got msg: " + msgOut2);
        Assert.assertNull(nonTxConsumer.receive(500));
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

    // Really nasty edge case to test sec 4.4.13, part 1
    //@Test - TODO - need bulk send to work
    public void testAmbiguousWriteCommit() throws JMSException
    {
        NevadoSession readerSession = createSession();
        NevadoSession session = getConnection().createSession(true, Session.SESSION_TRANSACTED);
        Queue testQueue = createTempQueue(session);
        NevadoMessage msg1 = session.createTextMessage(RandomData.readString());
        NevadoMessage msg2 = new TestBrokenMessage(session.createTextMessage(RandomData.readString()));
        MessageProducer producer = session.createProducer(testQueue);
        producer.send(msg1);
        producer.send(msg2);
        boolean exceptionThrown = false;
        try {
            session.commit();
        }
        catch (Throwable t)
        {
            exceptionThrown = true;
        }
        Assert.assertTrue("Bomb message didn't work", exceptionThrown);

        MessageConsumer consumer = readerSession.createConsumer(testQueue);
        Assert.assertNull("Got a message, but shouldn't have gotten one after ambiguous commit", consumer.receive(500));
        session.rollback();
        TextMessage msg3 = session.createTextMessage(RandomData.readString());
        producer.send(msg3);
        session.commit();

        TextMessage msgOut = (TextMessage)consumer.receive(1000);
        Assert.assertNotNull(msgOut);
        Assert.assertEquals(msg3, msgOut);
    }
}
