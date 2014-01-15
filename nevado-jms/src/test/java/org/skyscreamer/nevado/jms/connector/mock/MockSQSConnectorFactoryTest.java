package org.skyscreamer.nevado.jms.connector.mock;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;

public class MockSQSConnectorFactoryTest {

    private static final String ACCESS_KEY = "ACCESS_KEY";
    private static final String SECRET_KEY = "SECRET_KEY";
    private static final String QUEUE_NAME = "QUEUE_NAME";
    private static final String QUEUE_MESSAGE_PAYLOAD = "QUEUE_MESSAGE_PAYLOAD";
    private static final long DEFAULT_TIMEOUT = 50L;

    private MockSQSConnectorFactory _mockSQSConnectorFactory;
    private MockSQSConnector _mockSQSConnector;
    private NevadoQueue _queue;
    private NevadoTextMessage _queueMessage;
    private NevadoConnection _nevadoConnection;


    @Before
    public void setUp() throws Exception {
        _mockSQSConnectorFactory = new MockSQSConnectorFactory();
        _mockSQSConnector = (MockSQSConnector) _mockSQSConnectorFactory.getInstance(ACCESS_KEY, SECRET_KEY, null);
        _nevadoConnection = new NevadoConnection(_mockSQSConnector);
        _nevadoConnection.start();

        _queue = _mockSQSConnector.createQueue(QUEUE_NAME);

        _queueMessage = new NevadoTextMessage();
        _queueMessage.setText(QUEUE_MESSAGE_PAYLOAD);
    }

    @Test
    public void testFactoryWillSendAndReceiveAMessage() throws Exception {
        _mockSQSConnector.sendMessage(_queue, _queueMessage);

        NevadoMessage resultMessage = _mockSQSConnector.receiveMessage(_nevadoConnection, _queue, DEFAULT_TIMEOUT);

        Assert.assertNotNull("Message was not returned", resultMessage);

        Assert.assertTrue("Message was not a text message", NevadoTextMessage.class.isAssignableFrom(resultMessage.getClass()));

        Assert.assertEquals("Messages were not equal", _queueMessage.getText() , ((NevadoTextMessage)resultMessage).getText());
    }

    @Test
    public void testResetWillResetTheInternalConnectorBackToEmpty() throws Exception {
        _mockSQSConnector.sendMessage(_queue, _queueMessage);

        _mockSQSConnectorFactory.reset();

        NevadoMessage resultMessage = _mockSQSConnector.receiveMessage(_nevadoConnection, _queue, DEFAULT_TIMEOUT);

        Assert.assertNull("queue message was returned and internal connector should have been reset", resultMessage);
    }

}
