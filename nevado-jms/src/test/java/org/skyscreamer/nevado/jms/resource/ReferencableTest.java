package org.skyscreamer.nevado.jms.resource;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Random;

/**
 * Test our referenceable objects, destination and connectionfactory (sec. 4.2)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ReferencableTest extends AbstractJMSTest {
    private static final String TEST_ACCESS_KEY = RandomData.readString();
    private static final String TEST_SECRET_KEY = RandomData.readString();
    private static final String TEST_CLIENT_ID = RandomData.readString();
    private static final Integer TEST_DELIVERY_MODE = (int)RandomData.readShort();
    private static final Integer TEST_PRIORITY = (new Random()).nextInt(10);
    private static final Long TEST_TTL = (long)RandomData.readInt();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testConnectionFactory() throws NamingException, MalformedURLException {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        connectionFactory.setAwsAccessKey(TEST_ACCESS_KEY);
        connectionFactory.setAwsSecretKey(TEST_SECRET_KEY);
        connectionFactory.setClientID(TEST_CLIENT_ID);
        connectionFactory.setOverrideJMSDeliveryMode(TEST_DELIVERY_MODE);
        connectionFactory.setOverrideJMSPriority(TEST_PRIORITY);
        connectionFactory.setOverrideJMSTTL(TEST_TTL);
        Context ctx = getContext();
        ctx.bind("testConnectionFactory", connectionFactory);
        NevadoConnectionFactory testConnectionFactory = (NevadoConnectionFactory)ctx.lookup("testConnectionFactory");
        Assert.assertEquals(connectionFactory.getAwsAccessKey(), testConnectionFactory.getAwsAccessKey());
        Assert.assertEquals(connectionFactory.getAwsSecretKey(), testConnectionFactory.getAwsSecretKey());
        Assert.assertEquals(connectionFactory.getClientID(), testConnectionFactory.getClientID());
        Assert.assertEquals(connectionFactory.getJMSDeliveryMode(), testConnectionFactory.getJMSDeliveryMode());
        Assert.assertEquals(connectionFactory.getJMSPriority(), testConnectionFactory.getJMSPriority());
        Assert.assertEquals(connectionFactory.getJMSTTL(), testConnectionFactory.getJMSTTL());
        ctx.close();
    }

    private Context getContext() throws MalformedURLException, NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        env.put(Context.PROVIDER_URL, folder.getRoot().toURI().toURL().toString());
        Context ctx = new InitialContext(env);
        Assert.assertNotNull(ctx);
        return ctx;
    }

    @Test
    public void testQueue() throws NamingException, MalformedURLException {
        NevadoQueue queue = new NevadoQueue("testQueue");
        Context ctx = getContext();
        ctx.bind("testQueue", queue);
        Queue testQueue = (Queue)ctx.lookup("testQueue");
        Assert.assertEquals(queue, testQueue);
        ctx.close();
    }

    @Test
    public void testTopic() throws NamingException, MalformedURLException {
        NevadoTopic topic = new NevadoTopic("testTopic");
        Context ctx = getContext();
        ctx.bind("testTopic", topic);
        Topic testTopic = (Topic)ctx.lookup("testTopic");
        Assert.assertEquals(topic, testTopic);
        ctx.close();
    }

    @Test(expected = NamingException.class)
    public void testTemporaryQueue() throws JMSException, NamingException, MalformedURLException {
        TemporaryQueue temporaryQueue = createTempQueue(createSession());
        Context ctx = getContext();
        ctx.bind("tempQueue", temporaryQueue);
    }

    @Test(expected = NamingException.class)
    public void testTemporaryTopic() throws JMSException, NamingException, MalformedURLException {
        TemporaryTopic temporaryTopic = createTempTopic(createSession());
        Context ctx = getContext();
        ctx.bind("tempTopic", temporaryTopic);
    }
}
