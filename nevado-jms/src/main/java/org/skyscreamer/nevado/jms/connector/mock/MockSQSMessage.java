package org.skyscreamer.nevado.jms.connector.mock;

import org.skyscreamer.nevado.jms.connector.SQSMessage;
import org.skyscreamer.nevado.jms.util.RandomData;

/**
 * Mock implementation of an SQSMessage object
 *
 * @see MockSQSQueue
 * @see MockSQSConnector
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MockSQSMessage implements SQSMessage {
    private final String _messageId = RandomData.readString();
    private final String _receiptHandle = RandomData.readString();
    private final String _body;
    private long _visibleAfter = 0;

    public MockSQSMessage(String body) {
        _body = body;
    }

    @Override
    public String getReceiptHandle() {
        return _receiptHandle;
    }

    @Override
    public String getMessageBody() {
        return _body;
    }

    @Override
    public String getMessageId() {
        return _messageId;
    }

    public void setVisibleAfter(long visibleAfter) {
        _visibleAfter = visibleAfter;
    }

    public boolean isVisible() {
        return System.currentTimeMillis() >= _visibleAfter;
    }
}
