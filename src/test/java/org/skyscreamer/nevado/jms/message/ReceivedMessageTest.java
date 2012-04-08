package org.skyscreamer.nevado.jms.message;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;
import javax.jms.TextMessage;

/**
 * Test for section 3.10, 3.11.1, and 3.11.2 of the JMS 1.1 Specification.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ReceivedMessageTest extends AbstractJMSTest {
    @Test
    public void testInitialState() throws JMSException {
        TextMessage msg = createSession().createTextMessage();
        msg.setStringProperty("a", "b");
        msg.setText("test");
        msg.setJMSCorrelationID("X");
        TextMessage msgOut = (TextMessage)sendAndReceive(msg);
        Assert.assertEquals("b", msgOut.getStringProperty("a"));
        Assert.assertEquals("test", msgOut.getText());
        Assert.assertEquals("X", msgOut.getJMSCorrelationID());

        // Test setting header
        msgOut.setJMSCorrelationID("Y");
        Assert.assertEquals("Y", msgOut.getJMSCorrelationID());

        // Test setting property
        try {
            msgOut.setStringProperty("c", "d");
            Assert.fail("Should have thrown exception on property write");
        }
        catch (MessageNotWriteableException e) {
            // Expected
        }
        msgOut.clearProperties();
        msgOut.setStringProperty("c", "d");
        Assert.assertEquals("d", msgOut.getStringProperty("c"));

        // Test setting body
        try {
            msgOut.setText("something else");
            Assert.fail("Should have thrown exception on body write");
        }
        catch (MessageNotWriteableException e) {
            // Expected
        }
        msgOut.clearBody();
        msgOut.setText("new text");
        Assert.assertEquals("new text", msgOut.getText());
    }

    @Test
    public void testClearBodyDoesNotClearProperties() throws JMSException {
        TextMessage msg = createSession().createTextMessage();
        msg.setStringProperty("a", "b");
        msg.setText("test");
        TextMessage msgOut = (TextMessage)sendAndReceive(msg);
        msgOut.clearBody();
        Assert.assertEquals("b", msgOut.getStringProperty("a"));
    }
}
