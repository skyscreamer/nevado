package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.util.TestExceptionListener;
import org.skyscreamer.nevado.jms.util.TestMessageListener;

import javax.jms.*;

/**
 * Test JMS 1.1, sec 4.3.8
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ExceptionListenerTest extends AbstractJMSTest {
    @Test
    public void testExceptionListener() throws JMSException, InterruptedException {
        TestExceptionListener exceptionListener = new TestExceptionListener();
        getConnection().setExceptionListener(exceptionListener);
        TestMessageListener messageListener = new TestMessageListener(false);
        NevadoSession session = createSession();
        Queue tempQueue = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(tempQueue);
        consumer.setMessageListener(messageListener);
        breakSession(session);
        _log.info("Give the async runner 300ms to run");
        Thread.sleep(300);
        Assert.assertTrue(exceptionListener.getExceptions().size() > 0);
    }
}
