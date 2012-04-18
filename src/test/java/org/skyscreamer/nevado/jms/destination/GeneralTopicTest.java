package org.skyscreamer.nevado.jms.destination;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.*;

import javax.jms.*;

/**
 * General topic checks
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class GeneralTopicTest {
    @Test
    public void testCommonAndPTPAreSameImplementation()
    {
        Assert.assertTrue(ConnectionFactory.class.isAssignableFrom(NevadoConnectionFactory.class));
        Assert.assertTrue(TopicConnectionFactory.class.isAssignableFrom(NevadoConnectionFactory.class));
        Assert.assertTrue(Connection.class.isAssignableFrom(NevadoConnection.class));
        Assert.assertTrue(TopicConnection.class.isAssignableFrom(NevadoConnection.class));
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

}
