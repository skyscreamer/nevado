package org.skyscreamer.nevado.jms.destination;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;
import java.util.Collection;
import java.util.UUID;

/**
 * Tests Nevado's implementation of TemporaryQueue.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TemporaryQueueTest extends AbstractJMSTest {
    @Test
    public void testTemporaryQueue() throws Exception {
        NevadoSession session = createSession();
        TemporaryQueue temporaryQueue = session.createTemporaryQueue();
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        session.createProducer(temporaryQueue).send(testMessage);
        Message msgOut = session.createConsumer(temporaryQueue).receive();
        Assert.assertTrue(msgOut instanceof TextMessage);
        Assert.assertEquals("Message body not equal", testMessage.getText(), ((TextMessage) msgOut).getText());
    }

    @Test(expected = InvalidDestinationException.class)
    public void testTemporaryQueueAcrossConnections() throws Exception
    {
        NevadoSession session = createSession();
        TemporaryQueue temporaryQueue = session.createTemporaryQueue();
        Connection theWrongConnection = createConnection(getConnectionFactory());
        Session theWrongSession = theWrongConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = theWrongSession.createConsumer(temporaryQueue);
    }

    @Test
    public void testTemporaryQueueSuffix() throws Exception
    {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        String temporaryQueueSuffix = UUID.randomUUID().toString();
        Assert.assertTrue(temporaryQueueSuffix.length() > 0);
        connectionFactory.setTemporaryQueueSuffix(temporaryQueueSuffix);
        Connection connection = createConnection(connectionFactory);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue queue = session.createTemporaryQueue();
        Assert.assertTrue(queue.getQueueName().endsWith(temporaryQueueSuffix));
        connection.close();
    }

    @Test
    public void testDeleteUnusedTemporaryQueues() throws Exception {
        NevadoConnection conn1;
        String suffix1;
        NevadoConnection conn2;
        String suffix2;

        {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            suffix1 = UUID.randomUUID().toString();
            connectionFactory.setTemporaryQueueSuffix(suffix1);
            conn1 = createConnection(connectionFactory);
        }
        {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            suffix2 = UUID.randomUUID().toString();
            connectionFactory.setTemporaryQueueSuffix(suffix2);
            conn2 = createConnection(connectionFactory);
        }

        try {
            conn1.start();
            conn2.start();
            Assert.assertTrue(conn1.listAllTemporaryQueues().isEmpty());
            Assert.assertTrue(conn2.listAllTemporaryQueues().isEmpty());
            NevadoTemporaryQueue queue1 = conn1.createSession(false, Session.AUTO_ACKNOWLEDGE).createTemporaryQueue();
            NevadoTemporaryQueue queue2 = conn2.createSession(false, Session.AUTO_ACKNOWLEDGE).createTemporaryQueue();
            Assert.assertTrue(conn1.listAllTemporaryQueues().contains(queue1));
            Assert.assertTrue(conn2.listAllTemporaryQueues().contains(queue2));
            conn1.deleteUnusedTemporaryQueues(suffix2 + "X");
            Assert.assertTrue(conn1.listAllTemporaryQueues().contains(queue1));
            Assert.assertTrue(conn2.listAllTemporaryQueues().contains(queue2));
            conn1.deleteUnusedTemporaryQueues(suffix2);
            Assert.assertTrue(conn1.listAllTemporaryQueues().contains(queue1));
            Assert.assertFalse(conn2.listAllTemporaryQueues().contains(queue2));
        }
        finally {
            try {
                conn1.close();
            }
            catch (Throwable t) {
                _log.error("Unable to close connection 1", t);
            }
            try {
                conn2.close();
            }
            catch (Throwable t) {
                _log.error("Unable to close connection 1", t);
            }
        }
    }

    // Because the queues returned by SQS ListQueues is not synchronous with creation and deletion of queues, it is
    // too flaky to test in a quick, automated fashion.  This could be done with thie very slow test
    // but we'll leave it disabled so our overall suite can remain fast.
    //@Test
    public void testTemporaryQueueDeletion() throws Exception {
        NevadoSession session = createSession();
        TemporaryQueue temporaryQueue = session.createTemporaryQueue();
        Thread.sleep(15000);
        Collection<NevadoTemporaryQueue> allTemporaryQueues = getConnection().listAllTemporaryQueues();
        Assert.assertTrue("Temporary queue should exist", allTemporaryQueues.contains(temporaryQueue));
        getConnection().close();
        Thread.sleep(60000);
        allTemporaryQueues = getConnection().listAllTemporaryQueues();
        Assert.assertFalse("Temporary queue should not exist", allTemporaryQueues.contains(temporaryQueue));
    }
}
