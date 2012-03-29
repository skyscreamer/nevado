package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/28/12
 * Time: 9:37 PM
 */
public class JMSDestinationTest extends AbstractJMSTest {
    @Test
    public void testAssign() throws JMSException {
        clearTestQueue();
        Message msg = getSession().createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        getSession().createProducer(getTestQueue()).send(msg);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(getTestQueue(), msg.getJMSDestination());
        Assert.assertEquals(getTestQueue(), msgOut.getJMSDestination());
    }
}
