package org.skyscreamer.nevado.jms.properties;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Test for section 3.5.7 of the JMS 1.1 Specification.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ClearPropertiesTest extends AbstractJMSTest {
    private static final String TEST_BODY = "Some text";

    @Test
    public void testClear() throws JMSException {
        TextMessage msg = getSession().createTextMessage();
        msg.setStringProperty("a", "b");
        msg.setText(TEST_BODY);
        TextMessage msgOut = (TextMessage)sendAndReceive(msg);
        msgOut.clearProperties();
        Assert.assertNull(msgOut.getStringProperty("a"));
        msgOut.setStringProperty("c", "d");
        Assert.assertEquals("d", msgOut.getStringProperty("c"));
        Assert.assertEquals(TEST_BODY, msgOut.getText());
    }
}
