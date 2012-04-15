package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.*;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.skyscreamer.nevado.jms.util.TestMessageListener;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Test closing a connection (JMS 1.1, sec. 4.3.5)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionCloseTest extends AbstractJMSTest {
    @Test
    public void testPendingMessages() throws JMSException
    {
        Connection connection = getConnection();
        NevadoSession session = createSession();
        connection.stop();
        TextMessage msg = session.createTextMessage(RandomData.readString());
        Queue testQueue = createTempQueue(session);
        session.createProducer(testQueue).send(msg);
        MessageConsumer consumer = session.createConsumer(testQueue);
        TestMessageListener listener = new TestMessageListener();
        consumer.setMessageListener(listener);
        connection.start();
        connection.close();
        Assert.assertNull(listener.getMessage(100));
    }

    @Test(expected = IllegalStateException.class)
    public void testReceiveAfterClose() throws JMSException
    {
        Connection connection = getConnection();
        NevadoSession session = createSession();
        MessageConsumer consumer = session.createConsumer(new NevadoQueue("unusedQueue"));
        connection.close();
        consumer.receiveNoWait();
    }

    /**
     * Test that a connection rolls back a transaction when it is closed.
     *
     * @throws JMSException
     */
    @Test
    public void testRollbackTransaction() throws JMSException
    {
        // A temporary queue (but not a TemporaryQueue!) is needed here since we're testing across connections
        NevadoQueue testQueue = new NevadoQueue("testQueue" + RandomData.readInt());

        // Do some stuff, roll it back
        Connection testConnection = createConnection(getConnectionFactory());
        testConnection.start();
        Session controlSession = createSession();
        TextMessage msg1 = controlSession.createTextMessage(RandomData.readString());
        controlSession.createProducer(testQueue).send(msg1);
        Session session = testConnection.createSession(true, Session.SESSION_TRANSACTED);
        TextMessage msgOut = (TextMessage)session.createConsumer(testQueue).receive();
        Assert.assertEquals(msg1.getText(), msgOut.getText());
        TextMessage msg2 = session.createTextMessage(RandomData.readString());
        session.createProducer(testQueue).send(msg2);
        testConnection.close();

        // Confirm the rollback worked
        Session testSession = createSession();
        MessageConsumer consumer = testSession.createConsumer(testQueue);
        TextMessage afterTxMsg = (TextMessage)consumer.receive(1000);
        Assert.assertNotNull(afterTxMsg);
        Assert.assertEquals(msg1.getText(), afterTxMsg.getText());
        Assert.assertNull(consumer.receive(100));

        // Clean up
        deleteQueue(testQueue);
    }

    @Test
    public void testRecursiveClose() throws JMSException
    {
        NevadoConnection testConnection = getConnection();
        NevadoSession session = (NevadoSession)testConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue testQueue = createTempQueue(session);
        NevadoMessageConsumer consumer = session.createConsumer(testQueue);
        NevadoMessageProducer producer = session.createProducer(testQueue);
        testConnection.close();
        Assert.assertFalse(testConnection.isRunning());
        Assert.assertTrue(testConnection.isClosed());
        Assert.assertTrue(session.isClosed());
        Assert.assertTrue(consumer.isClosed());
        Assert.assertTrue(producer.isClosed());
    }

    @Test(expected = IllegalStateException.class)
    public void testAcknowledgeMessage() throws JMSException
    {
        NevadoConnection testConnection = getConnection();
        NevadoSession session = (NevadoSession)testConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Queue testQueue = createTempQueue(session);
        session.createProducer(testQueue).send(session.createMessage());
        Message msg = session.createConsumer(testQueue).receive(1000);
        Assert.assertNotNull(msg);
        testConnection.close();
        msg.acknowledge();
    }

    @Test
    public void reClose() throws JMSException
    {
        NevadoConnection testConnection = getConnection();
        testConnection.close();
        testConnection.close(); // This must not throw an exception
    }

    @Test(expected = IllegalStateException.class)
    public void testSessionAfterClose() throws JMSException
    {
        NevadoConnection testConnection = getConnection();
        NevadoSession session = (NevadoSession)testConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        testConnection.close();
        session.createMessage();
    }

    @Test(expected = IllegalStateException.class)
    public void testConsumerAfterClose() throws JMSException
    {
        NevadoConnection testConnection = getConnection();
        NevadoSession session = (NevadoSession)testConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(createTempQueue(session));
        testConnection.close();
        consumer.receiveNoWait();
    }

    @Test(expected = IllegalStateException.class)
    public void testProducerAfterClose() throws JMSException
    {
        NevadoConnection testConnection = getConnection();
        NevadoSession session = (NevadoSession)testConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Message msg = session.createMessage();
        MessageProducer producer = session.createProducer(createTempQueue(session));
        testConnection.close();
        producer.send(msg);
    }

}
