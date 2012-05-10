package org.skyscreamer.nevado.jms.performance;

/**
 * Tally card for perfomance run
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
class PerformanceTally {
    private volatile int _numOutOfOrder = 0;
    private volatile int _numMessagesReceived = 0;
    private volatile int _numDupMessages = 0;
    private volatile int _numMissedMessages = 0;

    public void add(PerformanceTally tally) {
        _numOutOfOrder += tally.getNumOutOfOrder();
        _numMessagesReceived += tally.getNumMessagesReceived();
        _numDupMessages += tally.getNumDupMessages();
        _numMissedMessages += tally.getNumMissedMessages();
    }

    public int getNumOutOfOrder() {
        return _numOutOfOrder;
    }

    public void setNumOutOfOrder(int numOutOfOrder) {
        _numOutOfOrder = numOutOfOrder;
    }

    public int getNumMessagesReceived() {
        return _numMessagesReceived;
    }

    public void setNumMessagesReceived(int numMessagesReceived) {
        _numMessagesReceived = numMessagesReceived;
    }

    public int getNumDupMessages() {
        return _numDupMessages;
    }

    public void setNumDupMessages(int numDupMessages) {
        _numDupMessages = numDupMessages;
    }

    public int getNumMissedMessages() {
        return _numMissedMessages;
    }

    public void setNumMissedMessages(int numMissedMessages) {
        _numMissedMessages = numMissedMessages;
    }
}
