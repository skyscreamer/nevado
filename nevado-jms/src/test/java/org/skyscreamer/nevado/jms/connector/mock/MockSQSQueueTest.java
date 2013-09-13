package org.skyscreamer.nevado.jms.connector.mock;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.nevado.jms.connector.SQSMessage;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;

public class MockSQSQueueTest {

    private static final String QUEUE_NAME = "QUEUE_NAME";
    private static final String MESSAGE_BODY = "MESSAGE_BODY";

    private MockSQSQueue _mockSQSQueue;

    private MockSQSConnector _connector;

    private NevadoQueue _queue;

    @Before
    public void setup(){
        _queue = new NevadoQueue(QUEUE_NAME);
        _connector = new MockSQSConnector();
        _mockSQSQueue = new MockSQSQueue(_connector, _queue);
    }

    @Test
    public void testQueueWorksAsExpectedForNonResetQueue() throws Exception {
        _mockSQSQueue.sendMessage(MESSAGE_BODY);
        SQSMessage sqsMessage = _mockSQSQueue.receiveMessage();
        Assert.assertNotNull("Message was not returned from internal list", sqsMessage);
        Assert.assertEquals("Message bodies were not equal", MESSAGE_BODY, sqsMessage.getMessageBody());
        sqsMessage = _mockSQSQueue.receiveMessage();
        Assert.assertNull("Message was returned but internal list should have been empty", sqsMessage);
    }

    @Test
    public void testQueueCanBeReset() throws Exception {
        _mockSQSQueue.sendMessage(MESSAGE_BODY);
        _mockSQSQueue.reset();
        SQSMessage sqsMessage = _mockSQSQueue.receiveMessage();
        Assert.assertNull("Message was returned but internal list should have been empty", sqsMessage);
    }

    @Test
    public void testQueueResetDoesNotThrowExceptionIfQueueIsDeleted() throws Exception {
        _mockSQSQueue.sendMessage(MESSAGE_BODY);
        _mockSQSQueue.deleteQueue();
        _mockSQSQueue.reset();
    }
}
