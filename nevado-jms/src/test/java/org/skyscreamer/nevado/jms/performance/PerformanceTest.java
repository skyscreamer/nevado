package org.skyscreamer.nevado.jms.performance;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;

/**
 * Stub to run some basic performance tests
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PerformanceTest extends AbstractJMSTest {
    private static final int NUM_MESSAGES = 100;
    private static final int MESSAGE_SIZE = 1;
    private static final int NUM_THREADS = 1;
    private static final int MESSAGE_SEND_DELAY_MS = 0;

    @Autowired private PerformanceService _performanceService;

    @Test
    public void testSequence() throws JMSException, InterruptedException {
        PerformanceResult result = _performanceService.runSample(getConnection(), NUM_MESSAGES, MESSAGE_SIZE,
                NUM_THREADS, MESSAGE_SEND_DELAY_MS);
        _log.warn("Sent " + result.getNumMessagesSent() + " messages in " + result.getTotalSendTimeMs()
                + " ms - " + (result.getTotalSendTimeMs() / result.getNumMessagesSent()) + " ms/msg, "
                + ((result.getNumMessagesSent()*1000) / result.getTotalSendTimeMs()) + " msg/sec "
                + "(including an introduced delay of " + MESSAGE_SEND_DELAY_MS + " ms)");
        _log.warn("Received  " + result.getNumMessagesReceived() + " messages in " + result.getTotalReceiveTimeMs()
                + " ms - " + (result.getTotalReceiveTimeMs() / result.getNumMessagesSent()) + " ms/msg, "
                + ((result.getNumMessagesSent()*1000) / result.getTotalReceiveTimeMs()) + " msg/sec ");
        _log.warn("Messages out of order: " + result.getNumOutOfOrder());
        _log.warn("Duplicate messages: " + result.getNumDupMessages());
        _log.warn("Missed messages: " + result.getNumMissedMessages());
        _log.warn("Message size: " + result.getMessageSize());
        _log.warn("# of threads: " + result.getNumThreads());
        Assert.assertEquals(0, result.getNumDupMessages());
        Assert.assertEquals(0, result.getNumMissedMessages());
    }
}
