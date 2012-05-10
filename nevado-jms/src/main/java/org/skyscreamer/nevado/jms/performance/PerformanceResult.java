package org.skyscreamer.nevado.jms.performance;

/**
 * Holds the results for a performance sample
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PerformanceResult {
    private final int _numMessagesSent;
    private final int _numMessagesReceived;
    private final int _messageSize;
    private final int _numThreads;
    private final int _numDupMessages;
    private final int _numMissedMessages;
    private final int _numOutOfOrder;
    private final long _totalSendTimeMs;
    private final long _totalReceiveTimeMs;

    public PerformanceResult(int numMessagesSent, PerformanceTally tally, int messageSize, int numThreads,
                             long sendTime, long receiveTime)
    {
        _numMessagesSent = numMessagesSent;
        _numMessagesReceived = tally.getNumMessagesReceived();
        _messageSize = messageSize;
        _numThreads = numThreads;
        _numDupMessages = tally.getNumDupMessages();
        _numMissedMessages = tally.getNumMissedMessages();
        _numOutOfOrder = tally.getNumOutOfOrder();
        _totalSendTimeMs = sendTime;
        _totalReceiveTimeMs = receiveTime;
    }

    // Getters
    public int getNumMessagesSent() {
        return _numMessagesSent;
    }

    public int getNumMessagesReceived() {
        return _numMessagesReceived;
    }

    public int getMessageSize() {
        return _messageSize;
    }

    public int getNumThreads() {
        return _numThreads;
    }

    public int getNumDupMessages() {
        return _numDupMessages;
    }

    public int getNumMissedMessages() {
        return _numMissedMessages;
    }

    public int getNumOutOfOrder() {
        return _numOutOfOrder;
    }

    public long getTotalSendTimeMs() {
        return _totalSendTimeMs;
    }

    public long getTotalReceiveTimeMs() {
        return _totalReceiveTimeMs;
    }
}
