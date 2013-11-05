package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.CloudCredentials;
import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSConnector;
import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSCredentials;
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
        CloudCredentials credentials = ((NevadoConnectionFactory)getConnectionFactory()).getCloudCredentials();
        if (credentials instanceof AmazonAwsSQSCredentials) {
            ((AmazonAwsSQSCredentials) credentials).setAwsSQSEndpoint("http://sqs.us-east-1.amazonaws.com");
            ((AmazonAwsSQSCredentials) credentials).setAwsSNSEndpoint("http://sns.us-east-1.amazonaws.com");
            Connection connection = getConnection();
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

    @Test(expected = ResourceAllocationException.class)
    public void testBadSQSEndpoint() throws JMSException
    {
        CloudCredentials credentials = ((NevadoConnectionFactory)getConnectionFactory()).getCloudCredentials();
        if (credentials instanceof AmazonAwsSQSCredentials) {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            ((AmazonAwsSQSCredentials) credentials).setAwsSQSEndpoint(MockSQSConnectorFactory.BAD_ENDPOINT_URL);
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

    @Test(expected = ResourceAllocationException.class)
    public void testBadSNSEndpoint() throws JMSException
    {
        CloudCredentials credentials = ((NevadoConnectionFactory)getConnectionFactory()).getCloudCredentials();
        if (credentials instanceof AmazonAwsSQSCredentials) {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            ((AmazonAwsSQSCredentials) credentials).setAwsSNSEndpoint(MockSQSConnectorFactory.BAD_ENDPOINT_URL);
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
}
