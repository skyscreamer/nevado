package org.skyscreamer.nevado.jms.connector.mock;

import org.skyscreamer.nevado.jms.connector.SQSMessage;
import org.skyscreamer.nevado.jms.connector.SQSQueue;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.JMSException;
import java.util.LinkedList;

/**
 * Mock implementation of an SQSQueue.  Intended for testing and development.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MockSQSQueue implements SQSQueue {
    private static final long DEFAULT_MESSAGE_VISIBILITY = 120000;
    private final LinkedList<MockSQSMessage> _messageList = new LinkedList<MockSQSMessage>();
    private final String _queueARN = RandomData.readString();
    private final NevadoQueue _queue;
    private final MockSQSConnector _connector;
    private boolean _isDeleted = false;

    public MockSQSQueue(MockSQSConnector connector, NevadoQueue queue) {
        _queue = queue;
        _connector = connector;
    }

    public NevadoQueue getQueue() {
        return _queue;
    }

    @Override
    public synchronized String sendMessage(String body) throws JMSException {
        checkIsDeleted();
        MockSQSMessage message = new MockSQSMessage(body);
        _messageList.addLast(message);
        return message.getMessageId();
    }

    @Override
    public synchronized void setMessageVisibilityTimeout(String sqsReceiptHandle, int timeout) throws JMSException {
        checkIsDeleted();
        for(MockSQSMessage message : _messageList)
        {
            if (sqsReceiptHandle.equals(message.getReceiptHandle()))
            {
                message.setVisibleAfter(System.currentTimeMillis() + timeout);
                return;
            }
        }
        throw new JMSException("No message with receipt handle: " + sqsReceiptHandle);
    }

    @Override
    public String getQueueARN() throws JMSException {
        checkIsDeleted();
        return _queueARN;
    }

    @Override
    public void setPolicy(String policy) throws JMSException {
        checkIsDeleted();
        // nop
    }

    @Override
    public synchronized void deleteQueue() throws JMSException {
        checkIsDeleted();
        _connector.removeQueue(_queue);
        _isDeleted = true;
    }

    @Override
    public synchronized void deleteMessage(String sqsReceiptHandle) throws JMSException {
        checkIsDeleted();
        MockSQSMessage messageToDelete = null;
        for(MockSQSMessage message : _messageList)
        {
            if (sqsReceiptHandle.equals(message.getReceiptHandle()))
            {
                messageToDelete = message;
                break;
            }
        }
        if (messageToDelete == null) {
            throw new JMSException("No message with receipt handle: " + sqsReceiptHandle);
        }
        _messageList.remove(messageToDelete);
    }

    @Override
    public synchronized SQSMessage receiveMessage() throws JMSException {
        checkIsDeleted();
        SQSMessage nextMessage = null;
        for(MockSQSMessage message : _messageList)
        {
            if (message.isVisible())
            {
                message.setVisibleAfter(System.currentTimeMillis() + DEFAULT_MESSAGE_VISIBILITY);
                nextMessage = message;
                break;
            }
        }
        return nextMessage;
    }

    private void checkIsDeleted() throws JMSException {
        if (_isDeleted)
        {
            throw new JMSException("This queue was deleted");
        }
    }
}
