package org.skyscreamer.nevado.jms.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the BackoffSleeper.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class BackoffSleeperTest {
    @Test
    public void testBackoffSleeper() throws InterruptedException {
        BackoffSleeper sleeper = new BackoffSleeper(50, 230, 2.0);
        long now = System.currentTimeMillis();
        sleeper.sleep();
        Assert.assertEquals(now + 50, System.currentTimeMillis(), 50);
        sleeper.sleep();
        Assert.assertEquals(now + 150, System.currentTimeMillis(), 100);
        sleeper.sleep();
        Assert.assertEquals(now + 350, System.currentTimeMillis(), 100);
        sleeper.sleep();
        Assert.assertEquals(now + 580, System.currentTimeMillis(), 100);
        sleeper.sleep();
        Assert.assertEquals(now + 810, System.currentTimeMillis(), 100);
        sleeper.reset();
        Assert.assertEquals(now + 810, System.currentTimeMillis(), 100);
        sleeper.sleep();
        Assert.assertEquals(now + 860, System.currentTimeMillis(), 100);
    }
}
