package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/28/12
 * Time: 8:37 AM
 */
public class JMSExpirationTest extends AbstractJMSTest {
    @Test
    public void testControl() throws JMSException {
        clearTestQueue();
        Message msg = getSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        Assert.assertEquals(0, msg.getJMSExpiration());
    }

    @Test
    public void testSetExpiration() throws JMSException {
        clearTestQueue();
        Message msg = getSession().createMessage();
        MessageProducer msgProducer = getSession().createProducer(getTestQueue());
        msgProducer.setDisableMessageID(true);
        msgProducer.send(msg, Message.DEFAULT_DELIVERY_MODE, Message.DEFAULT_PRIORITY, 60000);
        Assert.assertEquals(System.currentTimeMillis() + 60000, msg.getJMSExpiration(), 500);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(System.currentTimeMillis() + 60000, msgOut.getJMSExpiration(), 500);
    }

    @Test
    public void testExpire() throws JMSException, InterruptedException {
        clearTestQueue();
        Message msgToExpire = getSession().createMessage();
        Message msgWithoutExpire = getSession().createMessage();
        MessageProducer msgProducer = getSession().createProducer(getTestQueue());
        msgProducer.send(msgToExpire, Message.DEFAULT_DELIVERY_MODE, Message.DEFAULT_PRIORITY, 10);
        msgProducer.send(msgWithoutExpire);
        Thread.sleep(10);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals("Should skip the expired message", msgWithoutExpire.getJMSMessageID(),
                msgOut.getJMSMessageID());
    }
}
