package org.skyscreamer.nevado.jms.message;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.*;
import java.io.Serializable;

/**
 * Test that mutable data (basically byte[]) is not just being passed as a reference.  (Per JMS 1.1 sec. 3.12)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class CopyOfDataTest extends AbstractJMSTest {
    @Test
    public void testObjectMessage() throws JMSException {
        ObjectMessage objMsg = createSession().createObjectMessage();
        TestObject testObj = new TestObject();
        testObj.setValue(1);
        objMsg.setObject(testObj);
        testObj.setValue(2);
        TestObject retrievedObject = (TestObject)objMsg.getObject();
        Assert.assertEquals(1, retrievedObject.getValue());
        retrievedObject.setValue(3);
        Assert.assertEquals(1, ((TestObject)objMsg.getObject()).getValue());
    }

    @Test
    public void testBytesMessage() throws JMSException {
        byte[] bytes = {1, 2, 3};
        NevadoBytesMessage bytesMessage = (NevadoBytesMessage) createSession().createBytesMessage();
        bytesMessage.writeBytes(bytes);
        bytes[1] = 10; // This should not get reflected in the message
        bytesMessage.onSend();
        byte[] bytesOut = new byte[3];
        bytesMessage.readBytes(bytesOut, 3);
        Assert.assertEquals(2, bytesOut[1]);
    }

    @Test
    public void testStreamMessage() throws JMSException {
        byte[] bytes = {1, 2, 3};
        NevadoStreamMessage streamMessage = (NevadoStreamMessage) createSession().createStreamMessage();
        streamMessage.writeBytes(bytes);
        bytes[1] = 10; // This should not get reflected in the message
        streamMessage.onSend();
        byte[] bytesOut = new byte[3];
        streamMessage.readBytes(bytesOut);
        Assert.assertEquals(2, bytesOut[1]);
    }

    @Test
    public void testMapMessage() throws JMSException {
        byte[] bytes = {1, 2, 3};
        MapMessage mapMessage = createSession().createMapMessage();
        mapMessage.setBytes("key", bytes);
        bytes[1] = 10; // This should not get reflected in the message
        byte[] bytesOut = mapMessage.getBytes("key");
        Assert.assertEquals(2, bytesOut[1]);

        // Make sure the read array is not a reference to the internal data
        bytesOut[1] = 20;
        Assert.assertEquals(2, mapMessage.getBytes("key")[1]);
    }

    private static class TestObject implements Serializable {
        private int _value;

        public int getValue() {
            return _value;
        }

        public void setValue(int value) {
            _value = value;
        }
    }
}
