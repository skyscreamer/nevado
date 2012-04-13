package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/29/12
 * Time: 9:18 AM
 */
public class JMSPriorityTest extends AbstractJMSTest {
    @Test
    public void testDefault() throws JMSException {
        Message msg = createSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        Assert.assertEquals(Message.DEFAULT_PRIORITY, msg.getJMSPriority());
        Assert.assertEquals(Message.DEFAULT_PRIORITY, msgOut.getJMSPriority());
    }

    @Test
    public void testAssign() throws JMSException {
        Message msg1 = createSession().createMessage();
        Queue tempQueue = createTempQueue();
        MessageProducer msgProducer = createSession().createProducer(tempQueue);
        msgProducer.send(msg1, Message.DEFAULT_DELIVERY_MODE, 0, Message.DEFAULT_TIME_TO_LIVE);
        Message msgOut = createSession().createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(0, msg1.getJMSPriority());
        Assert.assertEquals(0, msgOut.getJMSPriority());

        Message msg2 = createSession().createMessage();
        msgProducer.send(msg2, Message.DEFAULT_DELIVERY_MODE, 9, Message.DEFAULT_TIME_TO_LIVE);
        msgOut = createSession().createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(9, msg2.getJMSPriority());
        Assert.assertEquals(9, msgOut.getJMSPriority());
    }
}
