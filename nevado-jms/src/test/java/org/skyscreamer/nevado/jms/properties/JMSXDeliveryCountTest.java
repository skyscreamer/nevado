package org.skyscreamer.nevado.jms.properties;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.message.JMSXProperty;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

public class JMSXDeliveryCountTest extends AbstractJMSTest {
    @Test
    public void testJMSXDeliveryCount() throws JMSException, InterruptedException {
        // Send messages
        NevadoConnection connection = getConnection();
        NevadoSession session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        TextMessage msg = session.createTextMessage(RandomData.readString());
        Queue tempQueue = createTempQueue(session);
        MessageProducer producer = session.createProducer(tempQueue);
        producer.send(msg);

        // Get messages
        MessageConsumer consumer = session.createConsumer(tempQueue);
        NevadoTextMessage msgOut = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(1, msgOut.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertEquals(msg.getText(), msgOut.getText());

        // Don't acknowledge, grab it again
        session.close();
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        consumer = session.createConsumer(tempQueue);
        msgOut = (NevadoTextMessage)consumer.receive();

        session.close();
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        consumer = session.createConsumer(tempQueue);
        msgOut = (NevadoTextMessage)consumer.receive();

        session.close();
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        consumer = session.createConsumer(tempQueue);
        msgOut = (NevadoTextMessage)consumer.receive();

        Assert.assertEquals(4, msgOut.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertTrue(msgOut.getJMSRedelivered());
        Assert.assertEquals(msg.getText(), msgOut.getText());

        // Recover and it will iterate
        session.recover();
        msgOut = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(5, msgOut.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertTrue(msgOut.getJMSRedelivered());
        Assert.assertEquals(msg.getText(), msgOut.getText());

        // Kill, repeat.  It stays at 5, unfortunately, since the last delivery iteration was local
        session.close();
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        consumer = session.createConsumer(tempQueue);
        msgOut = (NevadoTextMessage)consumer.receive();
        Assert.assertEquals(5, msgOut.getIntProperty(JMSXProperty.JMSXDeliveryCount + ""));
        Assert.assertTrue(msgOut.getJMSRedelivered());
        Assert.assertEquals(msg.getText(), msgOut.getText());
        msgOut.acknowledge();
    }
}
