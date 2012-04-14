package org.skyscreamer.nevado.jms.destination;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;
import java.util.Collection;

/**
 * TODO - Description
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

    // Because the queues returned by SQS ListQueues is not synchronous with creation and deletion of queues, it is
    // too flaky to test in a quick, automated fashion.  This could be done with thie very slow test
    // but we'll leave it disabled so our overall suite can remain fast.  (Besides we'll have a backup cleanup method
    // to make this a little redundant.)
    //@Test
    public void testTemporaryQueueDeletion() throws Exception {
        NevadoSession session = createSession();
        TemporaryQueue temporaryQueue = session.createTemporaryQueue();
        Thread.sleep(15000);
        Collection<TemporaryQueue> allTemporaryQueues = getConnection().listAllTemporaryQueues();
        Assert.assertTrue("Temporary queue should exist", allTemporaryQueues.contains(temporaryQueue));
        session.close();
        Thread.sleep(60000);
        allTemporaryQueues = getConnection().listAllTemporaryQueues();
        Assert.assertFalse("Temporary queue should not exist", allTemporaryQueues.contains(temporaryQueue));
    }
}
