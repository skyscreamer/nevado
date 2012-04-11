package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.TestMessageListener;

import javax.jms.*;

/**
 * Tests transactional behavior for sessions, per JMS 1.1 Sec. 4.4.7
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class SessionTransactionTest extends AbstractJMSTest {
    @Test
    public void testTransaction() throws JMSException, InterruptedException {
        clearTestQueue();
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
        Assert.assertEquals(ctlMsg1.getText(), msg1.getText());
        Assert.assertEquals(ctlMsg2.getText(), msg2.getText());
        Assert.assertEquals(ctlMsg3.getText(), msg3.getText());
        Assert.assertNull(txConsumer.receive(100));
        MessageProducer txProducer = txSession.createProducer(testProduceQueue);
        TextMessage rollbackMsg1 = txSession.createTextMessage(RandomData.readString());
        TextMessage rollbackMsg2 = txSession.createTextMessage(RandomData.readString());
        _log.info("These messages are going to be rolled back, so should never be seen again: "
            + rollbackMsg1 + " " + rollbackMsg2);
        txProducer.send(rollbackMsg1);
        txProducer.send(rollbackMsg2);

        // Test that nothing has been sent yet
        Assert.assertNull(consumer.receive(100));

        // Rollback, re-read and re-send
        txSession.rollback();
        msg1 = (NevadoTextMessage) txConsumer.receive();
        msg2 = (NevadoTextMessage) txConsumer.receive();
        msg3 = (NevadoTextMessage) txConsumer.receive();
        Assert.assertEquals(ctlMsg1.getText(), msg1.getText());
        Assert.assertEquals(ctlMsg2.getText(), msg2.getText());
        Assert.assertEquals(ctlMsg3.getText(), msg3.getText());
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
        Assert.assertEquals(commitMsg1.getText(), msgOut1.getText());
        Assert.assertEquals(commitMsg2.getText(), msgOut2.getText());
    }
}
