package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

/**
 * TODO - Description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class DurableTopicTest extends AbstractJMSTest {
    @Test
    public void testDurableTopic() throws JMSException {
        String durableTopicName = "testTopicSub" + RandomData.readShort();
        String testTopicName = "testTopic" + RandomData.readShort();
        Session session = createSession();
        NevadoTopic topic = (NevadoTopic)session.createTopic(testTopicName);
        TopicSubscriber subscriber = session.createDurableSubscriber(topic, durableTopicName);
        TextMessage msg1 = session.createTextMessage(RandomData.readString());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        TextMessage msg3 = session.createTextMessage(RandomData.readString());
        MessageProducer producer = session.createProducer(topic);
        producer.send(msg1);
        Assert.assertEquals(msg1, subscriber.receive(1000));
        producer.send(msg2);
        producer.send(msg3);
        getConnection().close();

        NevadoConnection conn = createConnection(getConnectionFactory());
        conn.start();
        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        subscriber = session.createDurableSubscriber(topic, durableTopicName);
        Assert.assertEquals(msg2, subscriber.receive(1000));
        subscriber.close();
        session.unsubscribe(durableTopicName);
        session.close();

        subscriber = session.createDurableSubscriber(topic, durableTopicName);
        Assert.assertNull(subscriber.receive(500));

        conn.getSQSConnector().deleteTopic(topic);

    }

    @Test
    public void testUnsubscribeWithActiveSubscriber() throws JMSException {
        String durableTopicName = "testTopicSub" + RandomData.readShort();
        NevadoSession session = createSession();
        NevadoTopic topic = createTempTopic(session);
        TopicSubscriber subscriber = session.createDurableSubscriber(topic, durableTopicName);
        boolean throwsException = false;
        try {
            session.unsubscribe(durableTopicName);
        }
        catch(JMSException e) {
            throwsException = true;
        }

        // Clean up
        subscriber.close();
        session.unsubscribe(durableTopicName);
        if (!throwsException)
        {
            Assert.fail("Expected exception to be thrown when trying to unsubscribe an active topic subscription");
        }
    }

    @Test
    public void testUnsubscribeWithUnackedMsg() throws JMSException {
        String durableTopicName = "testTopicSub" + RandomData.readShort();
        NevadoSession session = getConnection().createSession(false, Session.CLIENT_ACKNOWLEDGE);
        NevadoTopic topic = createTempTopic(session);
        TopicSubscriber subscriber = session.createDurableSubscriber(topic, durableTopicName);
        session.createProducer(topic).send(session.createMessage());
        subscriber.receive(1000); // Don't acknowledge
        boolean throwsException = false;
        try {
            session.unsubscribe(durableTopicName);
        }
        catch(JMSException e) {
            throwsException = true;
        }

        // Clean up
        subscriber.close();
        session.unsubscribe(durableTopicName);
        if (!throwsException)
        {
            Assert.fail("Expected exception to be thrown when trying to unsubscribe a topic with an unacked msg");
        }
    }

    @Test
    public void testDoubleSubscribe() throws JMSException {
        String durableTopicName = "testTopicSub" + RandomData.readShort();
        NevadoSession session = createSession();
        NevadoTopic topic = createTempTopic(session);
        TopicSubscriber subscriber = session.createDurableSubscriber(topic, durableTopicName);
        boolean throwsException = false;
        try {
            session.createDurableSubscriber(topic, durableTopicName);
        }
        catch(JMSException e) {
            throwsException = true;
        }

        // Clean up
        subscriber.close();
        session.unsubscribe(durableTopicName);
        if (!throwsException)
        {
            Assert.fail("Expected exception to be thrown when trying to double-subscribe an durable topic");
        }

    }
}
