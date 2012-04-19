package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.*;

/**
 * Test MessageProducer (JMS 1.1, sec 4.6)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageProducerTest extends AbstractJMSTest {
    @Test
    public void testOverrides() throws JMSException {
        Session session = createSession();
        Queue testQueue = session.createTemporaryQueue();
        MessageProducer producer = session.createProducer(testQueue);
        producer.setTimeToLive(123000);
        producer.setPriority(9);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        long time = System.currentTimeMillis();
        producer.send(session.createMessage());
        Message msgOut = session.createConsumer(testQueue).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(time + 123000, msgOut.getJMSExpiration(), 100);
        Assert.assertEquals(9, msgOut.getJMSPriority());
        Assert.assertEquals(DeliveryMode.NON_PERSISTENT, msgOut.getJMSDeliveryMode());
    }
}
