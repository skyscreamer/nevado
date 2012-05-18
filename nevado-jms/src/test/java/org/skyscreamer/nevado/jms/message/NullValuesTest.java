package org.skyscreamer.nevado.jms.message;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.junit.Assert;

import javax.jms.*;

/**
 * Tests expected handling of null values in message headers and bodies.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NullValuesTest extends AbstractJMSTest {
    @Test
    public void testMessageHeadersAndProperties() throws JMSException {
        Message msg = createSession().createMessage();
        msg.setJMSCorrelationID(null);
        Assert.assertNull(msg.getJMSCorrelationID());
        msg.setJMSCorrelationIDAsBytes(null);
        Assert.assertNull(msg.getJMSCorrelationIDAsBytes());
        msg.setJMSDestination(null);
        Assert.assertNull(msg.getJMSDestination());
        msg.setJMSMessageID(null);
        Assert.assertNull(msg.getJMSMessageID());
        msg.setJMSReplyTo(null);
        Assert.assertNull(msg.getJMSReplyTo());
        msg.setJMSType(null);
        Assert.assertNull(msg.getJMSType());
        msg.setObjectProperty("key", null);
        Assert.assertNull(msg.getObjectProperty("key"));
        msg.setStringProperty("key", null);
        Assert.assertNull(msg.getObjectProperty("key"));
    }

    @Test(expected = NullPointerException.class)
    public void testBytesMessage() throws JMSException {
        BytesMessage bytesMessage = createSession().createBytesMessage();
        bytesMessage.writeBytes(null);
    }

    @Test
    public void testMapMessage() throws JMSException {
        MapMessage mapMsg = createSession().createMapMessage();
        mapMsg.setObject("key", null);
        Assert.assertNull(mapMsg.getObject("key"));
        mapMsg.setString("key", null);
        Assert.assertNull(mapMsg.getString("key"));
        mapMsg.setBytes("key", null);
        Assert.assertNull(mapMsg.getBytes("key"));
    }

    @Test
    public void testObjectMessage() throws JMSException {
        ObjectMessage objMsg = createSession().createObjectMessage();
        objMsg.setObject(null);
        Assert.assertNull(objMsg.getObject());
    }

    @Test
    public void testTextMessage() throws JMSException {
        TextMessage textMsg = createSession().createTextMessage();
        textMsg.setText(null);
        Assert.assertNull(textMsg.getText());
    }

    @Test
    public void testStreamMessage() throws JMSException {
        NevadoStreamMessage streamMsg = (NevadoStreamMessage) createSession().createStreamMessage();
        streamMsg.writeInt(1);
        streamMsg.writeString(null);
        streamMsg.writeInt(2);
        streamMsg.writeObject(null);
        streamMsg.writeInt(3);
        streamMsg.onSend();
        Assert.assertEquals(1, streamMsg.readInt());
        Assert.assertNull(streamMsg.readString());
        Assert.assertEquals(2, streamMsg.readInt());
        Assert.assertNull(streamMsg.readString());
        Assert.assertEquals(3, streamMsg.readInt());
    }

    // Technically a primitive array, byte[] should not be null
    @Test(expected = NullPointerException.class)
    public void testStreamMessageNullBytes() throws JMSException {
        NevadoStreamMessage streamMsg = (NevadoStreamMessage) createSession().createStreamMessage();
        streamMsg.writeBytes(null);
    }
}
