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
        clearTestQueue();
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Message msg = session.createMessage();
        session.createProducer(getTestQueue()).send(msg);
        NevadoMessage msgOut = (NevadoMessage)session.createConsumer(getTestQueue()).receive();
        Assert.assertTrue(msgOut.isAcknowledged());
    }

    @Test
    public void testAsyncAutoAcknowledge() throws JMSException, InterruptedException {
        clearTestQueue();
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Message msg = session.createMessage();
        session.createProducer(getTestQueue()).send(msg);
        TestMessageListener messageListener = new TestMessageListener();
        session.createConsumer(getTestQueue()).setMessageListener(messageListener);
        Thread.sleep(500);
        Assert.assertEquals(1, messageListener.getMessages().size());
        Assert.assertTrue(((NevadoMessage)messageListener.getMessages().get(0)).isAcknowledged());
        session.close();
    }

    @Test
    public void testDupsOkayAcknowledge() throws JMSException {
        clearTestQueue();
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        Message msg = session.createMessage();
        session.createProducer(getTestQueue()).send(msg);
        NevadoMessage msgOut = (NevadoMessage)session.createConsumer(getTestQueue()).receive();
        Assert.assertFalse(msgOut.isAcknowledged());
        msgOut.acknowledge();
    }

    @Test
    public void testClientAcknowledge() throws JMSException {
        clearTestQueue();

        // Send messages
        Connection connection = getConnection();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        TextMessage msg3 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(getTestQueue());
        producer.send(msg1);
        producer.send(msg2);
        producer.send(msg3);

        // Get messages
        MessageConsumer consumer = session.createConsumer(getTestQueue());
        NevadoTextMessage msgOut1 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut1.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        NevadoTextMessage msgOut2 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut2.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        NevadoTextMessage msgOut3 = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut3.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertFalse(msgOut1.isAcknowledged());
        Assert.assertEquals(msg1.getText(), msgOut1.getText());
        Assert.assertFalse(msgOut2.isAcknowledged());
        Assert.assertEquals(msg2.getText(), msgOut2.getText());
        Assert.assertFalse(msgOut3.isAcknowledged());
        Assert.assertEquals(msg3.getText(), msgOut3.getText());

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
        Assert.assertEquals(msg1.getText(), msgOut1.getText());
        Assert.assertFalse(msgOut2.isAcknowledged());
        Assert.assertEquals(msg2.getText(), msgOut2.getText());
        Assert.assertFalse(msgOut3.isAcknowledged());
        Assert.assertEquals(msg3.getText(), msgOut3.getText());

        // Acknowledging one should acknowledge the others
        msgOut2.acknowledge();
        Assert.assertTrue(msgOut1.isAcknowledged());
        Assert.assertTrue(msgOut2.isAcknowledged());
        Assert.assertTrue(msgOut3.isAcknowledged());
    }
}
