package org.skyscreamer.nevado.jms.facilities;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import javax.jms.IllegalStateException;

/**
 * Test Nevado implementation of queue browser
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class QueueBrowserTest extends AbstractJMSTest {
    @Test
    public void testQueueBrowser() {
        // TODO
    }

    @Test
    public void testBrowseEmptyQueue() {
        // TODO
    }

    @Test(expected = IllegalStateException.class)
    public void testAcknowledgeBrowsedMessage() {
        // TODO
    }
}
