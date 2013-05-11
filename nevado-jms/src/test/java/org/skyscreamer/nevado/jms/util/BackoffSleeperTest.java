package org.skyscreamer.nevado.jms.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the BackoffSleeper.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class BackoffSleeperTest {
    // Cloudbees needs coarse resolution because their boxes have heavy contention
    public static final int ACCEPTABLE_DELTA_MS = 50;

    @Test
    public void testBackoffSleeper() throws InterruptedException {
        BackoffSleeper sleeper = new BackoffSleeper(50, 230, 2.0);
        long now = System.currentTimeMillis();
        sleeper.sleep();
        Assert.assertEquals(now + 50, System.currentTimeMillis(), ACCEPTABLE_DELTA_MS);
        sleeper.sleep();
        Assert.assertEquals(now + 150, System.currentTimeMillis(), ACCEPTABLE_DELTA_MS);
        sleeper.sleep();
        Assert.assertEquals(now + 350, System.currentTimeMillis(), ACCEPTABLE_DELTA_MS);
        sleeper.sleep();
        Assert.assertEquals(now + 580, System.currentTimeMillis(), ACCEPTABLE_DELTA_MS);
        sleeper.sleep();
        Assert.assertEquals(now + 810, System.currentTimeMillis(), ACCEPTABLE_DELTA_MS);
        sleeper.reset();
        Assert.assertEquals(now + 810, System.currentTimeMillis(), ACCEPTABLE_DELTA_MS);
        sleeper.sleep();
        Assert.assertEquals(now + 860, System.currentTimeMillis(), ACCEPTABLE_DELTA_MS);
    }
}
