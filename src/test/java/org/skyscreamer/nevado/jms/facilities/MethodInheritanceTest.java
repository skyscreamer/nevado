package org.skyscreamer.nevado.jms.facilities;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Ensure correct behavior with method inheritance (JMS 1.1, sec 4.11)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MethodInheritanceTest extends AbstractJMSTest {
    @Test(expected = IllegalStateException.class)
    public void testCreateDurableConnectionConsumer() throws JMSException {
        QueueConnectionFactory connectionFactory = new NevadoConnectionFactory();
        QueueConnection connection = createQueueConnection(connectionFactory);
        connection.createDurableConnectionConsumer(null, null, null, null, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateDurableSubscriber1() throws JMSException {
        QueueConnectionFactory connectionFactory = new NevadoConnectionFactory();
        QueueConnection connection = createQueueConnection(connectionFactory);
        connection.start();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createDurableSubscriber(new NevadoTopic("unusedTopic"), null);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateDurableSubscriber2() throws JMSException {
        QueueConnectionFactory connectionFactory = new NevadoConnectionFactory();
        QueueConnection connection = createQueueConnection(connectionFactory);
        connection.start();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createDurableSubscriber(new NevadoTopic("unusedTopic"), null, null, false);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateTemporaryTopic() throws JMSException {
        QueueConnectionFactory connectionFactory = new NevadoConnectionFactory();
        QueueConnection connection = createQueueConnection(connectionFactory);
        connection.start();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createTemporaryTopic();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateTopic() throws JMSException {
        QueueConnectionFactory connectionFactory = new NevadoConnectionFactory();
        QueueConnection connection = createQueueConnection(connectionFactory);
        connection.start();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createTopic("unusedTopic");
    }

    @Test(expected = IllegalStateException.class)
    public void testUnsubscribe() throws JMSException {
        QueueConnectionFactory connectionFactory = new NevadoConnectionFactory();
        QueueConnection connection = createQueueConnection(connectionFactory);
        connection.start();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        session.unsubscribe("unusedTopic");
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateQueueBrowser1() throws JMSException {
        TopicConnectionFactory connectionFactory = new NevadoConnectionFactory();
        TopicConnection connection = createTopicConnection(connectionFactory);
        connection.start();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createBrowser(new NevadoQueue("unusedQueue"));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateQueueBrowser2() throws JMSException {
        TopicConnectionFactory connectionFactory = new NevadoConnectionFactory();
        TopicConnection connection = createTopicConnection(connectionFactory);
        connection.start();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createBrowser(new NevadoQueue("unusedQueue"), null);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateQueue() throws JMSException {
        TopicConnectionFactory connectionFactory = new NevadoConnectionFactory();
        TopicConnection connection = createTopicConnection(connectionFactory);
        connection.start();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createQueue("unusedQueue");
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateTemporaryQueue() throws JMSException {
        TopicConnectionFactory connectionFactory = new NevadoConnectionFactory();
        TopicConnection connection = createTopicConnection(connectionFactory);
        connection.start();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createTemporaryQueue();
    }
}
