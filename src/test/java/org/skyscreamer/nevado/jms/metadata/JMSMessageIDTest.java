package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.*;

public class JMSMessageIDTest extends AbstractJMSTest {
    @Test
    public void testAssign() throws JMSException {
        Message msg = createSession().createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        Message msgOut = sendAndReceive(msg);
        Assert.assertNotNull(msg.getJMSMessageID());
        Assert.assertTrue(msg.getJMSMessageID().startsWith("ID:"));
        Assert.assertEquals(msg.getJMSMessageID(), msgOut.getJMSMessageID());
    }
    
    @Test
    public void testDisable() throws JMSException {
        NevadoSession session = createSession();
        Message msg = session.createMessage();
        Queue tempQueue = createTempQueue(session);
        MessageProducer msgProducer = session.createProducer(tempQueue);
        msgProducer.setDisableMessageID(true);
        msgProducer.send(msg);
        Assert.assertNull(msg.getJMSMessageID());
        Message msgOut = session.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertNull(msgOut.getJMSMessageID());
    }
}
