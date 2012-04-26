package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.*;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/28/12
 * Time: 9:56 PM
 */
public class JMSDeliveryModeTest extends AbstractJMSTest {
    @Test
    public void testDefault() throws JMSException {
        Message msg = createSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        Assert.assertEquals(Message.DEFAULT_DELIVERY_MODE, msg.getJMSDeliveryMode());
        Assert.assertEquals(Message.DEFAULT_DELIVERY_MODE, msgOut.getJMSDeliveryMode());
    }

    @Test
    public void testAssign() throws JMSException {
        NevadoSession session = createSession();
        Message msg1 = session.createMessage();
        Queue tempQueue = createTempQueue(session);
        MessageProducer msgProducer = session.createProducer(tempQueue);
        msgProducer.send(msg1, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
        Message msgOut = session.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(DeliveryMode.NON_PERSISTENT, msg1.getJMSDeliveryMode());
        Assert.assertEquals(DeliveryMode.NON_PERSISTENT, msgOut.getJMSDeliveryMode());

        Message msg2 = session.createMessage();
        msgProducer.send(msg2, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
        msgOut = session.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(DeliveryMode.PERSISTENT, msg2.getJMSDeliveryMode());
        Assert.assertEquals(DeliveryMode.PERSISTENT, msgOut.getJMSDeliveryMode());
    }
}
