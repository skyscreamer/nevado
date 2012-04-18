package org.skyscreamer.nevado.jms.destination;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.*;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;

import javax.jms.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Test general behaviors from JMS 1.1, sec 5.1
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class GeneralQueueTest extends AbstractJMSTest {
    @Test
    public void testMixedMessages() throws JMSException
    {
        NevadoSession session = createSession();
        Queue testQueue = createTempQueue(session);
        MessageProducer producer = session.createProducer(testQueue);
        Set<Message> messagesIn = new HashSet<Message>();
        messagesIn.add(session.createBytesMessage());
        messagesIn.add(session.createMapMessage());
        messagesIn.add(session.createObjectMessage());
        messagesIn.add(session.createStreamMessage());
        messagesIn.add(session.createTextMessage());
        for(Message message : messagesIn)
        {
            producer.send(message);
        }

        MessageConsumer consumer = session.createConsumer(testQueue);
        Set<Message> messagesOut = new HashSet<Message>();
        for(int i = 0 ; i < messagesIn.size() ; ++i) {
            messagesOut.add(consumer.receive(1000));
        }
        for(Message message : messagesIn)
        {
            Assert.assertTrue("Did not get message of type " + message.getClass().getName() + " back",
                    messagesOut.contains(message));
        }
    }

    @Test
    public void testCommonAndPTPAreSameImplementation()
    {
        Assert.assertTrue(ConnectionFactory.class.isAssignableFrom(NevadoConnectionFactory.class));
        Assert.assertTrue(QueueConnectionFactory.class.isAssignableFrom(NevadoConnectionFactory.class));
        Assert.assertTrue(Connection.class.isAssignableFrom(NevadoConnection.class));
        Assert.assertTrue(QueueConnection.class.isAssignableFrom(NevadoConnection.class));
        Assert.assertTrue(Queue.class.isAssignableFrom(NevadoQueue.class));
        Assert.assertTrue(NevadoDestination.class.isAssignableFrom(NevadoQueue.class));
        Assert.assertTrue(Destination.class.isAssignableFrom(NevadoDestination.class));
        Assert.assertTrue(Session.class.isAssignableFrom(NevadoSession.class));
        Assert.assertTrue(QueueSession.class.isAssignableFrom(NevadoQueueSession.class));
        Assert.assertTrue(NevadoSession.class.isAssignableFrom(NevadoQueueSession.class));
        Assert.assertTrue(MessageProducer.class.isAssignableFrom(NevadoMessageProducer.class));
        Assert.assertTrue(QueueSender.class.isAssignableFrom(NevadoMessageProducer.class));
        Assert.assertTrue(MessageConsumer.class.isAssignableFrom(NevadoMessageConsumer.class));
        Assert.assertTrue(QueueReceiver.class.isAssignableFrom(NevadoMessageConsumer.class));
    }

}
