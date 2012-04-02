package org.skyscreamer.nevado.jms.properties;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Test for section 3.5.2 of the JMS 1.1 Specification.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PropertyValueTest extends AbstractJMSTest {
    private static final Object[] INVALID_VALUE_OBJECTS = {new Object(), new HashMap(), new NevadoQueue("X")};
    @Test
    public void testValidValues() throws JMSException {
        Message msg = getSession().createMessage();
        msg.setObjectProperty("boolean", true);
        msg.setObjectProperty("byte", new Byte("1"));
        msg.setObjectProperty("short", new Short("123"));
        msg.setObjectProperty("int", 1234567);
        msg.setObjectProperty("long", 12345678901L);
        msg.setObjectProperty("float", 123.456f);
        msg.setObjectProperty("double", 1234567890.0987654321d);
    }

    @Test
    public void testInvalidValues() throws JMSException {
        Message msg = getSession().createMessage();
        for (Object o : INVALID_VALUE_OBJECTS) {
            try {
                msg.setObjectProperty("otherObject", new Object());
            }
            catch (MessageFormatException e) {
                // Expected
                continue;
            }
            Assert.fail("Did not throw expected exception for value of type " + o.getClass().getName());
        }
    }
}
