package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.RandomData;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/30/12
 * Time: 8:16 AM
 */
public class ClientJMSHeadersTest extends AbstractJMSTest {
    @Test
    public void testAssign() throws JMSException {
        clearTestQueue();
        Message msg = createSession().createMessage();
        msg.setJMSCorrelationID(RandomData.readString());
        msg.setJMSReplyTo(new NevadoQueue("nosuchqueue"));
        msg.setJMSType(RandomData.readString());

        Message msgOut = sendAndReceive(msg);

        Assert.assertNotNull(msgOut.getJMSCorrelationID());
        Assert.assertEquals(msg.getJMSCorrelationID(), msgOut.getJMSCorrelationID());

        Assert.assertNotNull(msgOut.getJMSReplyTo());
        Assert.assertEquals(msg.getJMSReplyTo(), msgOut.getJMSReplyTo());

        Assert.assertNotNull(msgOut.getJMSType());
        Assert.assertEquals(msg.getJMSType(), msgOut.getJMSType());
    }
}
