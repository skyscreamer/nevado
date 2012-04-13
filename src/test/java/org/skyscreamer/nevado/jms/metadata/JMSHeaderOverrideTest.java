package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/30/12
 * Time: 8:43 AM
 */
public class JMSHeaderOverrideTest extends AbstractJMSTest {
    private NevadoConnectionFactory _overriddenConnectionFactory;
    private Session _overriddenSession;

    @Test
    public void testOverride() throws JMSException, IOException {
        _overriddenConnectionFactory = new NevadoConnectionFactory();
        _overriddenConnectionFactory.setOverrideJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        _overriddenConnectionFactory.setOverrideJMSPriority(9);
        _overriddenConnectionFactory.setOverrideJMSTTL(60000L);
        Connection conn = createConnection(_overriddenConnectionFactory);
        conn.start();
        _overriddenSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Message msg = _overriddenSession.createMessage();

        Queue tempQueue = createTempQueue();
        _overriddenSession.createProducer(tempQueue).send(msg);
        Message msgOut = _overriddenSession.createConsumer(tempQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();

        Assert.assertEquals(DeliveryMode.NON_PERSISTENT, msgOut.getJMSDeliveryMode());
        Assert.assertEquals(9, msgOut.getJMSPriority());
        Assert.assertEquals(System.currentTimeMillis() + 60000, msgOut.getJMSExpiration(), 1000);
    }
}
