package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.*;

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
        Session session = createSession();
        Message msg1 = session.createMessage();
        Queue tempQueue = createTempQueue(session);
        MessageProducer msgProducer = session.createProducer(tempQueue);
        msgProducer.send(msg1, Message.DEFAULT_DELIVERY_MODE, 0, Message.DEFAULT_TIME_TO_LIVE);
        Message msgOut = session.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(0, msg1.getJMSPriority());
        Assert.assertEquals(0, msgOut.getJMSPriority());

        Message msg2 = session.createMessage();
        msgProducer.send(msg2, Message.DEFAULT_DELIVERY_MODE, 9, Message.DEFAULT_TIME_TO_LIVE);
        msgOut = session.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(9, msg2.getJMSPriority());
        Assert.assertEquals(9, msgOut.getJMSPriority());
    }
}
