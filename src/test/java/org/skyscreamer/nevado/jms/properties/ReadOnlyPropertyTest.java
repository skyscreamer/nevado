package org.skyscreamer.nevado.jms.properties;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 4/1/12
 * Time: 4:03 PM
 */
public class ReadOnlyPropertyTest extends AbstractJMSTest {
    @Test(expected = MessageNotWriteableException.class)
    public void testReadonlyAfterSend() throws JMSException {
        Message msg = getSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        msgOut.setBooleanProperty("test", true);
    }

    @Test
    public void testReadonlyAfterSendThenClear() throws JMSException {
        Message msg = getSession().createMessage();
        Message msgOut = sendAndReceive(msg);
        msgOut.clearProperties();
        msgOut.setBooleanProperty("test", true);
    }
}
