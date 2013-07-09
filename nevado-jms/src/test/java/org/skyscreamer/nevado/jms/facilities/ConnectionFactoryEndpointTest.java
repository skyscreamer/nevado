package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.mock.MockSQSConnectorFactory;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

/**
 * Tests for overriding the endpoints
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionFactoryEndpointTest extends AbstractJMSTest {
    @Test
    public void testChangedEndpoints() throws JMSException
    {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        connectionFactory.setAwsSQSEndpoint("http://sqs.us-east-1.amazonaws.com");
        connectionFactory.setAwsSNSEndpoint("http://sns.us-east-1.amazonaws.com");
        Connection connection = createConnection(connectionFactory);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue queue = session.createTemporaryQueue();
        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        producer.send(testMessage);
        Message msgOut = consumer.receive(1000);
        Assert.assertNotNull(msgOut);
        Assert.assertTrue(msgOut instanceof TextMessage);
        Assert.assertEquals(testMessage, (TextMessage)msgOut);
        connection.close();
    }

    @Test(expected = ResourceAllocationException.class)
    public void testBadSQSEndpoint() throws JMSException
    {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        connectionFactory.setAwsSQSEndpoint(MockSQSConnectorFactory.BAD_ENDPOINT_URL);
        Connection connection = createConnection(connectionFactory);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue queue = session.createTemporaryQueue();
        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        producer.send(testMessage);
        Message msgOut = consumer.receive(1000);
        Assert.assertNotNull(msgOut);
        Assert.assertTrue(msgOut instanceof TextMessage);
        Assert.assertEquals(testMessage, (TextMessage)msgOut);
        connection.close();
    }

    @Test(expected = ResourceAllocationException.class)
    public void testBadSNSEndpoint() throws JMSException
    {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        connectionFactory.setAwsSNSEndpoint(MockSQSConnectorFactory.BAD_ENDPOINT_URL);
        Connection connection = createConnection(connectionFactory);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue queue = session.createTemporaryQueue();
        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        producer.send(testMessage);
        Message msgOut = consumer.receive(1000);
        Assert.assertNotNull(msgOut);
        Assert.assertTrue(msgOut instanceof TextMessage);
        Assert.assertEquals(testMessage, (TextMessage)msgOut);
        connection.close();
    }
}
