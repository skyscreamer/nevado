package org.skyscreamer.nevado.jms.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;

import javax.jms.*;

/**
 * Test JMS 1.1, sec 3.4.1
 */
public class JMSDestinationTest extends AbstractJMSTest {
    @Test
    public void testAssign() throws JMSException {
        Session session = createSession();
        Message msg = session.createMessage();
        Assert.assertNull(msg.getJMSMessageID());
        Queue tempQueue = createTempQueue(session);
        session.createProducer(tempQueue).send(msg);
        Message msgOut = session.createConsumer(tempQueue).receive(1000);
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertEquals(tempQueue, msg.getJMSDestination());
        Assert.assertEquals(tempQueue, msgOut.getJMSDestination());
    }

    @Test
    public void testTopic() throws JMSException {
        NevadoSession session = createSession();
        Message msg = session.createMessage();
        Topic tempTopic = createTempTopic(session);
        MessageProducer producer = session.createProducer(tempTopic);
        MessageConsumer consumer = session.createConsumer(tempTopic);
        producer.send(msg);
        Message msgOut = consumer.receive(1000);
        msgOut.acknowledge();
        Assert.assertEquals(tempTopic, msg.getJMSDestination());
        Assert.assertEquals(tempTopic, msgOut.getJMSDestination());
    }
}
