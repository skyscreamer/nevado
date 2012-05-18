package org.skyscreamer.nevado.jms.destination;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.*;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

/**
 * General topic checks
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class GeneralTopicTest extends AbstractJMSTest {
    @Test
    public void testCommonAndPubSubAreSameImplementation()
    {
        Assert.assertTrue(ConnectionFactory.class.isAssignableFrom(NevadoConnectionFactory.class));
        Assert.assertTrue(TopicConnectionFactory.class.isAssignableFrom(NevadoConnectionFactory.class));
        Assert.assertTrue(Connection.class.isAssignableFrom(NevadoConnection.class));
        Assert.assertTrue(TopicConnection.class.isAssignableFrom(NevadoTopicConnection.class));
        Assert.assertTrue(NevadoConnection.class.isAssignableFrom(NevadoTopicConnection.class));
        Assert.assertTrue(Topic.class.isAssignableFrom(NevadoTopic.class));
        Assert.assertTrue(NevadoDestination.class.isAssignableFrom(NevadoTopic.class));
        Assert.assertTrue(Destination.class.isAssignableFrom(NevadoDestination.class));
        Assert.assertTrue(Session.class.isAssignableFrom(NevadoSession.class));
        Assert.assertTrue(TopicSession.class.isAssignableFrom(NevadoTopicSession.class));
        Assert.assertTrue(NevadoSession.class.isAssignableFrom(NevadoTopicSession.class));
        Assert.assertTrue(MessageProducer.class.isAssignableFrom(NevadoMessageProducer.class));
        Assert.assertTrue(TopicPublisher.class.isAssignableFrom(NevadoMessageProducer.class));
        Assert.assertTrue(MessageConsumer.class.isAssignableFrom(NevadoMessageConsumer.class));
        Assert.assertTrue(TopicSubscriber.class.isAssignableFrom(NevadoMessageConsumer.class));
    }

    @Test
    public void testTopicFacilities() throws JMSException
    {
        TopicConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        TopicConnection connection = createTopicConnection(connectionFactory);
        connection.start();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryTopic topic = session.createTemporaryTopic();
        TopicPublisher sender = session.createPublisher(topic);
        TopicSubscriber receiver = session.createSubscriber(topic);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        sender.send(testMessage);
        Message msgOut = receiver.receive(1000);
        Assert.assertNotNull(msgOut);
        Assert.assertTrue(msgOut instanceof TextMessage);
        Assert.assertEquals(testMessage, (TextMessage)msgOut);
    }
}
