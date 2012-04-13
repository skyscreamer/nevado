package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/27/12
 * Time: 8:34 PM
 */
public class JMSTimestampTest extends AbstractJMSTest {
    @Test
    public void testAssign() throws JMSException {
        Message msg = createSession().createMessage();
        Assert.assertEquals(0, msg.getJMSTimestamp());
        Message msgOut = sendAndReceive(msg);
        Assert.assertEquals(new Date().getTime(), msg.getJMSTimestamp(), 5000);
    }

    @Test
    public void testDisable() throws JMSException {
        Message msg = createSession().createMessage();
        Assert.assertEquals(0, msg.getJMSTimestamp());
        Queue tempQueue = createTempQueue();
        MessageProducer msgProducer = createSession().createProducer(tempQueue);
        msgProducer.setDisableMessageTimestamp(true);
        msgProducer.send(msg);
        Message msgOut = createSession().createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(0, msg.getJMSTimestamp());
    }
}
