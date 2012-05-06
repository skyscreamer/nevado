package org.skyscreamer.nevado.jms.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO - Description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class BackoffSleeperTest {
    @Test
    public void testBackoffSleeper() {
        BackoffSleeper sleeper = new BackoffSleeper(10, 100, 2.0);
        long now = System.currentTimeMillis();
        sleeper.sleep();
        Assert.assertEquals(now + 10, System.currentTimeMillis(), 5);
        sleeper.sleep();
        Assert.assertEquals(now + 30, System.currentTimeMillis(), 5);
        sleeper.sleep();
        Assert.assertEquals(now + 70, System.currentTimeMillis(), 5);
        sleeper.sleep();
        Assert.assertEquals(now + 150, System.currentTimeMillis(), 5);
        sleeper.sleep();
        Assert.assertEquals(now + 250, System.currentTimeMillis(), 10);
        sleeper.reset();
        Assert.assertEquals(now + 250, System.currentTimeMillis(), 10);
        sleeper.sleep();
        Assert.assertEquals(now + 260, System.currentTimeMillis(), 10);
    }
}
