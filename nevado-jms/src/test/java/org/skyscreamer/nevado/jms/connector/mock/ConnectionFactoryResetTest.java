package org.skyscreamer.nevado.jms.connector.mock;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

public class ConnectionFactoryResetTest {
    private static final String ACCESS_KEY = "ACCESS_KEY";
    private static final String SECRET_KEY = "SECRET_KEY";
    private static final String QUEUE_NAME = "QUEUE_NAME";

    private MockSQSConnectorFactory _mockSQSConnectorFactory;

    @Before
    public void setUp() throws Exception {
        _mockSQSConnectorFactory = new MockSQSConnectorFactory();
    }

    @Test
    public void testResetWillEmptyQueue() throws Exception {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_mockSQSConnectorFactory);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue(QUEUE_NAME);
        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        producer.send(testMessage);

        _mockSQSConnectorFactory.reset();

        Message msgOut = consumer.receive(2000);
        Assert.assertNull(msgOut);
        connection.close();

    }
}