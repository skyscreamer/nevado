package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

public class JMSMessageIDTest extends AbstractJMSTest {
    @Test
    public void testAssign() throws JMSException {
        Message msg = getSession().createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        Message msgOut = sendAndReceive(msg);
        Assert.assertNotNull(msg.getJMSMessageID());
        Assert.assertTrue(msg.getJMSMessageID().startsWith("ID:"));
        Assert.assertEquals(msg.getJMSMessageID(), msgOut.getJMSMessageID());
    }
    
    @Test
    public void testDisable() throws JMSException {
        Message msg = getSession().createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        MessageProducer msgProducer = getSession().createProducer(getTestQueue());
        msgProducer.setDisableMessageID(true);
        msgProducer.send(msg);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertNull(msg.getJMSMessageID());
    }
}
