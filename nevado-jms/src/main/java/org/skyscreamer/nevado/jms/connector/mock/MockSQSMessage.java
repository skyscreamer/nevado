package org.skyscreamer.nevado.jms.connector.mock;

import org.skyscreamer.nevado.jms.connector.SQSMessage;
import org.skyscreamer.nevado.jms.util.RandomData;

import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, String> _attributes = new HashMap<String, String>();
    private long _visibleAfter = 0;
    private int _deliveryCount = 0;

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

    @Override
    public Map<String, String> getAttributes() {
        return _attributes;
    }

    public void setVisibleAfter(long visibleAfter) {
        _visibleAfter = visibleAfter;
    }

    public boolean isVisible() {
        return System.currentTimeMillis() >= _visibleAfter;
    }

    public int getDeliveryCount() {
        return _deliveryCount;
    }

    public void incrementDeliveryCount() {
        ++_deliveryCount;
    }
}
