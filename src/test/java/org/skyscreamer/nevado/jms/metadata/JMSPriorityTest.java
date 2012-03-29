package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/29/12
 * Time: 9:18 AM
 */
public class JMSPriorityTest extends AbstractJMSTest {
    @Test
    public void testDefault() throws JMSException {
        clearTestQueue();
        Message msg = getSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        Assert.assertEquals(Message.DEFAULT_PRIORITY, msg.getJMSPriority());
        Assert.assertEquals(Message.DEFAULT_PRIORITY, msgOut.getJMSPriority());
    }

    @Test
    public void testAssign() throws JMSException {
        Message msg1 = getSession().createMessage();
        MessageProducer msgProducer = getSession().createProducer(getTestQueue());
        msgProducer.send(msg1, Message.DEFAULT_DELIVERY_MODE, 0, Message.DEFAULT_TIME_TO_LIVE);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(0, msg1.getJMSPriority());
        Assert.assertEquals(0, msgOut.getJMSPriority());

        Message msg2 = getSession().createMessage();
        msgProducer.send(msg2, Message.DEFAULT_DELIVERY_MODE, 9, Message.DEFAULT_TIME_TO_LIVE);
        msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(9, msg2.getJMSPriority());
        Assert.assertEquals(9, msgOut.getJMSPriority());
    }
}
