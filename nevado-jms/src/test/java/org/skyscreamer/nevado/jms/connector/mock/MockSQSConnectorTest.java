package org.skyscreamer.nevado.jms.connector.mock;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;


public class MockSQSConnectorTest {

    private static final String QUEUE_NAME = "QUEUE_NAME";
    private static final String TOPIC_NAME = "TOPIC_NAME";
    private static final String QUEUE_MESSAGE_PAYLOAD = "QUEUE_MESSAGE_PAYLOAD";
    private static final String TOPIC_MESSAGE_PAYLOAD = "TOPIC_MESSAGE_PAYLOAD";
    private static final long DEFAULT_TIMEOUT = 50L;
    private static final String TEMP_QUEUE_NAME = "TEMP_QUEUE_NAME";

    private NevadoConnection _nevadoConnection;
    private MockSQSConnector _mockSQSConnector;

    private NevadoQueue _queue;

    private NevadoTopic _publishableTopic;
    private NevadoTopic _subscribableTopic;

    private NevadoTextMessage _queueMessage;
    private NevadoTextMessage _topicMessage;

    @Before
    public void setUp() throws Exception {
        _mockSQSConnector = new MockSQSConnector();
        _nevadoConnection = new NevadoConnection(_mockSQSConnector);
        _nevadoConnection.start();

        _queue = _mockSQSConnector.createQueue(QUEUE_NAME);

        _publishableTopic = _mockSQSConnector.createTopic(TOPIC_NAME);
        NevadoQueue topicEndpoint = _mockSQSConnector.createQueue(TEMP_QUEUE_NAME); // This should be a temp _queue but it is just a mock
        String subscriptionArn = _mockSQSConnector.subscribe(_publishableTopic, topicEndpoint);
        _subscribableTopic = new NevadoTopic(_publishableTopic, topicEndpoint, subscriptionArn, false);

        _queueMessage = new NevadoTextMessage();
        _queueMessage.setText(QUEUE_MESSAGE_PAYLOAD);
        _topicMessage = new NevadoTextMessage();
        _topicMessage.setText(TOPIC_MESSAGE_PAYLOAD);
    }

    @Test
    public void testMockSQSConnectorWillReturnMessagesWhenNotReset() throws Exception {
        _mockSQSConnector.sendMessage(_queue, _queueMessage);
        _mockSQSConnector.sendMessage(_publishableTopic, _topicMessage);

        NevadoMessage resultQueueMessage = _mockSQSConnector.receiveMessage(_nevadoConnection, _queue, DEFAULT_TIMEOUT);
        NevadoMessage resultTopicMessage = _mockSQSConnector.receiveMessage(_nevadoConnection, _subscribableTopic, DEFAULT_TIMEOUT);

        Assert.assertNotNull("queue message was not returned", resultQueueMessage);
        Assert.assertNotNull("topic message was not returned", resultTopicMessage);

        Assert.assertTrue("queue message was not a text message", NevadoTextMessage.class.isAssignableFrom(resultQueueMessage.getClass()));
        Assert.assertTrue("topic message was not a text message", NevadoTextMessage.class.isAssignableFrom(resultTopicMessage.getClass()));

        Assert.assertEquals("queue messages were not equal", _queueMessage.getText() , ((NevadoTextMessage)resultQueueMessage).getText());
        Assert.assertEquals("topic messages were not equal", _topicMessage.getText(), ((NevadoTextMessage)resultTopicMessage).getText());
    }

    @Test
    public void testMockSQSConnectorWillBeEmptyWhenReset() throws Exception {
        _mockSQSConnector.sendMessage(_queue, _queueMessage);
        _mockSQSConnector.sendMessage(_publishableTopic, _topicMessage);

        _mockSQSConnector.reset();

        NevadoMessage resultQueueMessage = _mockSQSConnector.receiveMessage(_nevadoConnection, _queue, DEFAULT_TIMEOUT);
        NevadoMessage resultTopicMessage = _mockSQSConnector.receiveMessage(_nevadoConnection, _subscribableTopic, DEFAULT_TIMEOUT);

        Assert.assertNull("queue message was returned and connector should have been reset", resultQueueMessage);
        Assert.assertNull("topic message was returned and connector should have been reset", resultTopicMessage);
    }

}
