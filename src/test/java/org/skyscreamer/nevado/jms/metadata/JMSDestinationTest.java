package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/28/12
 * Time: 9:37 PM
 */
public class JMSDestinationTest extends AbstractJMSTest {
    @Test
    public void testAssign() throws JMSException {
        Session session = createSession();
        Message msg = session.createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        Queue tempQueue = createTempQueue(session);
        session.createProducer(tempQueue).send(msg);
        Message msgOut = session.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(tempQueue, msg.getJMSDestination());
        Assert.assertEquals(tempQueue, msgOut.getJMSDestination());
    }
}
