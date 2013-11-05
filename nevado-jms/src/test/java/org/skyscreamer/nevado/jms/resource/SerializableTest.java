package org.skyscreamer.nevado.jms.resource;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.mock.MockCredentials;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.SerializeUtil;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Random;

/**
 * Tests for serializable (JMS 1.1, Sec 4.2)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class SerializableTest extends AbstractJMSTest {
    private static final String TEST_CREDENTIALS = RandomData.readString();
    private static final String TEST_SECRET_KEY = RandomData.readString();
    private static final String TEST_CLIENT_ID = RandomData.readString();
    private static final Integer TEST_DELIVERY_MODE = (int)RandomData.readShort();
    private static final Integer TEST_PRIORITY = (new Random()).nextInt(10);
    private static final Long TEST_TTL = (long)RandomData.readInt();

    @Test
    public void testConnectionFactory() throws IOException {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory();
        connectionFactory.setCloudCredentials(new MockCredentials(TEST_CREDENTIALS));
        connectionFactory.setClientID(TEST_CLIENT_ID);
        connectionFactory.setOverrideJMSDeliveryMode(TEST_DELIVERY_MODE);
        connectionFactory.setOverrideJMSPriority(TEST_PRIORITY);
        connectionFactory.setOverrideJMSTTL(TEST_TTL);
        NevadoConnectionFactory testConnectionFactory =
                (NevadoConnectionFactory) SerializeUtil.deserialize(SerializeUtil.serialize(connectionFactory));
        Assert.assertEquals(TEST_CREDENTIALS, ((MockCredentials)testConnectionFactory.getCloudCredentials()).getTestData());
        Assert.assertEquals(connectionFactory.getClientID(), testConnectionFactory.getClientID());
        Assert.assertEquals(connectionFactory.getJMSDeliveryMode(), testConnectionFactory.getJMSDeliveryMode());
        Assert.assertEquals(connectionFactory.getJMSPriority(), testConnectionFactory.getJMSPriority());
        Assert.assertEquals(connectionFactory.getJMSTTL(), testConnectionFactory.getJMSTTL());
    }

    @Test
    public void testQueue() throws IOException, JMSException {
        NevadoQueue queue = createTempQueue(createSession());
        NevadoQueue testQueue = (NevadoQueue) SerializeUtil.deserialize(SerializeUtil.serialize(queue));
        Assert.assertEquals(queue, testQueue);
    }

    @Test
    public void testTopic() throws IOException, JMSException {
        NevadoTopic topic = createTempTopic(createSession());
        NevadoTopic testTopic = (NevadoTopic) SerializeUtil.deserialize(SerializeUtil.serialize(topic));
        Assert.assertEquals(topic, testTopic);
    }
}
