package org.skyscreamer.nevado.jms.properties;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;

/**
 * Test for section 3.5.3 of the JMS 1.1 Specification.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ReadOnlyPropertyTest extends AbstractJMSTest {
    @Test(expected = MessageNotWriteableException.class)
    public void testReadonlyAfterSend() throws JMSException {
        Message msg = createSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        msgOut.setBooleanProperty("test", true);
    }

    @Test
    public void testReadonlyAfterSendThenClear() throws JMSException {
        Message msg = createSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        msgOut.clearProperties();
        msgOut.setBooleanProperty("test", true);
    }
}
