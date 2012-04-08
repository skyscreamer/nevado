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
        clearTestQueue();
        Message msg = createSession().createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        Message msgOut = sendAndReceive(msg);
        Assert.assertNotNull(msg.getJMSMessageID());
        Assert.assertTrue(msg.getJMSMessageID().startsWith("ID:"));
        Assert.assertEquals(msg.getJMSMessageID(), msgOut.getJMSMessageID());
    }
    
    @Test
    public void testDisable() throws JMSException {
        clearTestQueue();
        Message msg = createSession().createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        MessageProducer msgProducer = createSession().createProducer(getTestQueue());
        msgProducer.setDisableMessageID(true);
        msgProducer.send(msg);
        Message msgOut = createSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertNull(msg.getJMSMessageID());
    }
}
