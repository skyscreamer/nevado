package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.message.JMSXProperty;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.TestMessageListener;

import javax.jms.*;
import java.util.Random;

/**
 * Test message acknowledgement in different modes (JMS 1.1, Sec 4.4.11)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageAcknowledgementTest extends AbstractJMSTest {
    @Test
    public void testAutoAcknowledge() throws JMSException {
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Message msg = session.createMessage();
        Queue tempQueue = createTempQueue(session);
        session.createProducer(tempQueue).send(msg);
        NevadoMessage msgOut = (NevadoMessage)session.createConsumer(tempQueue).receive();
        Assert.assertTrue(msgOut.isAcknowledged());
    }

    @Test
    public void testAsyncAutoAcknowledge() throws JMSException, InterruptedException {
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Message msg = session.createMessage();
        Queue tempQueue = createTempQueue(session);
        session.createProducer(tempQueue).send(msg);
        TestMessageListener messageListener = new TestMessageListener();
        session.createConsumer(tempQueue).setMessageListener(messageListener);
        Thread.sleep(500);
        Assert.assertEquals(1, messageListener.getMessages().size());
        Assert.assertTrue(((NevadoMessage)messageListener.getMessages().get(0)).isAcknowledged());
        session.close();
    }

    @Test
    public void testDupsOkayAcknowledge() throws JMSException {
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Message msg = session.createMessage();
        Queue tempQueue = createTempQueue(session);
        session.createProducer(tempQueue).send(msg);
        NevadoMessage msgOut = (NevadoMessage)session.createConsumer(tempQueue).receive();
        Assert.assertFalse(msgOut.isAcknowledged());
        msgOut.acknowledge();
    }

    @Test
    public void testClientAcknowledge() throws JMSException {
        // Send messages
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        TextMessage msg3 = session.createTextMessage(RandomData.readString());
        Queue tempQueue = createTempQueue(session);
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(msg1);
        producer.send(msg2);
        producer.send(msg3);

        // Get messages
        MessageConsumer consumer = session.createConsumer(tempQueue);
        NevadoTextMessage msgOut1 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut1.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        NevadoTextMessage msgOut2 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut2.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        NevadoTextMessage msgOut3 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut3.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertFalse(msgOut1.isAcknowledged());
        Assert.assertFalse(msgOut2.isAcknowledged());
        Assert.assertFalse(msgOut3.isAcknowledged());
        compareTextMessages(new TextMessage[] {msg1, msg2, msg3}, new TextMessage[] {msgOut1, msgOut2, msgOut3});

        // Recover and replay
        session.recover();
        msgOut1 = (NevadoTextMessage)consumer.receive();
        msgOut2 = (NevadoTextMessage)consumer.receive();
        msgOut3 = (NevadoTextMessage)consumer.receive();
        Assert.assertTrue(msgOut1.getJMSRedelivered());
        Assert.assertTrue(msgOut2.getJMSRedelivered());
        Assert.assertTrue(msgOut3.getJMSRedelivered());
        Assert.assertEquals(2, msgOut1.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertEquals(2, msgOut2.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertEquals(2, msgOut3.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertFalse(msgOut1.isAcknowledged());
        Assert.assertFalse(msgOut2.isAcknowledged());
        Assert.assertFalse(msgOut3.isAcknowledged());
        compareTextMessages(new TextMessage[] {msg1, msg2, msg3}, new TextMessage[] {msgOut1, msgOut2, msgOut3});

        // Acknowledging one should acknowledge the others
        msgOut2.acknowledge();
        Assert.assertTrue(msgOut1.isAcknowledged());
        Assert.assertTrue(msgOut2.isAcknowledged());
        Assert.assertTrue(msgOut3.isAcknowledged());
    }

    @Test
    public void testPartialClientAcknowledge() throws JMSException {
        // Send messages
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        TextMessage msg3 = session.createTextMessage(RandomData.readString());
        Queue tempQueue = createTempQueue(session);
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(msg1);
        producer.send(msg2);
        producer.send(msg3);

        // Get messages
        MessageConsumer consumer = session.createConsumer(tempQueue);
        NevadoTextMessage msgOut1 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut1.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        NevadoTextMessage msgOut2 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut2.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        NevadoTextMessage msgOut3 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut3.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertFalse(msgOut1.isAcknowledged());
        Assert.assertFalse(msgOut2.isAcknowledged());
        Assert.assertFalse(msgOut3.isAcknowledged());
        compareTextMessages(new TextMessage[] {msg1, msg2, msg3}, new TextMessage[] {msgOut1, msgOut2, msgOut3});

        // Recover and replay (partially)
        session.recover();
        NevadoTextMessage msgAfterRecover1 = (NevadoTextMessage)consumer.receive();
        NevadoTextMessage msgAfterRecover2 = (NevadoTextMessage)consumer.receive();
        Assert.assertTrue(msgAfterRecover1.getJMSRedelivered());
        Assert.assertTrue(msgAfterRecover2.getJMSRedelivered());
        Assert.assertEquals(2, msgAfterRecover1.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertEquals(2, msgAfterRecover2.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertFalse(msgAfterRecover1.isAcknowledged());
        Assert.assertFalse(msgAfterRecover2.isAcknowledged());
        compareTextMessages(new TextMessage[] {msgOut1, msgOut2}, new TextMessage[] {msgAfterRecover1, msgAfterRecover2});

        // Acknowledging one should acknowledge the others
        msgAfterRecover2.acknowledge();
        Assert.assertTrue(msgAfterRecover1.isAcknowledged());
        Assert.assertTrue(msgAfterRecover2.isAcknowledged());

        // The third message should be back on the queue
        NevadoTextMessage msgAfterAck = (NevadoTextMessage)consumer.receive(500);
        // TODO - This isn't supported yet.  In this edge case, the message is reset on the queue, but there is no way
        //        to indicate a redelivery because you can't edit the message in the queue
        // Assert.assertTrue(msgAfterAck.getJMSRedelivered());
        // Assert.assertEquals(2, msgAfterAck.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertFalse(msgAfterAck.isAcknowledged());
        Assert.assertEquals(msgOut3.getText(), msgAfterAck.getText());
        msgAfterAck.acknowledge();
        Assert.assertTrue(msgAfterAck.isAcknowledged());
    }
}
