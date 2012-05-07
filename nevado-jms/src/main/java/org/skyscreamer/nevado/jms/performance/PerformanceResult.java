package org.skyscreamer.nevado.jms.performance;

/**
 * Holds the results for a performance sample
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PerformanceResult {
    private final int _numMessagesSent;
    private final int _numMessagesReceived;
    private final int _numDupMessages;
    private final int _numMissedMessages;
    private final int _numOutOfOrder;
    private final long _totalSendTimeMs;
    private final long _totalReceiveTimeMs;

    public PerformanceResult(int numMessagesSent, int numMessagesReceived, int numDupMessages, int numMissedMessages,
                             int numOutOfOrder, long totalSendTimeMs, long totalReceiveTimeMs)
    {
        _numMessagesSent = numMessagesSent;
        _numMessagesReceived = numMessagesReceived;
        _numDupMessages = numDupMessages;
        _numMissedMessages = numMissedMessages;
        _numOutOfOrder = numOutOfOrder;
        _totalSendTimeMs = totalSendTimeMs;
        _totalReceiveTimeMs = totalReceiveTimeMs;
    }

    // Getters
    public int getNumMessagesSent() {
        return _numMessagesSent;
    }

    public int getNumMessagesReceived() {
        return _numMessagesReceived;
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
