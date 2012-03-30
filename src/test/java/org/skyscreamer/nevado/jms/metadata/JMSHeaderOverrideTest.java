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
        _overriddenSession = _overriddenConnectionFactory.createConnection(getAwsAccessKey(), getAwsSecretKey())
                .createSession(false, Session.AUTO_ACKNOWLEDGE);

        clearTestQueue();
        Message msg = _overriddenSession.createMessage();

        _overriddenSession.createProducer(getTestQueue()).send(msg);
        Message msgOut = _overriddenSession.createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();

        Assert.assertEquals(DeliveryMode.NON_PERSISTENT, msgOut.getJMSDeliveryMode());
        Assert.assertEquals(9, msgOut.getJMSPriority());
        Assert.assertEquals(System.currentTimeMillis() + 60000, msgOut.getJMSExpiration(), 1000);
    }
}
