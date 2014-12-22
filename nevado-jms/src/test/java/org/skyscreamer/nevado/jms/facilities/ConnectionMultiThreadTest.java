package org.skyscreamer.nevado.jms.facilities;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;

import java.util.Random;

/**
 * Nothing beats code reviews to ensure concurrency, but this provides a little added confidence.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionMultiThreadTest extends AbstractJMSTest {
    private static final int NUM_SESSIONS = 10;
    private static final int NUM_THREADS = 100;
    private static final int NUM_ITERATIONS = 100;

    @Test(timeout = 120000)
    public void testStopStartALot() throws Exception {
        // Make a lot of sessions
        for(int i = 0 ; i < NUM_SESSIONS; ++i) {
            createSession();
        }
        _log.info("Created " + NUM_SESSIONS + " sessions");

        Thread[] threads = new Thread[NUM_THREADS];
        ConnectionOpenClose[] runnables = new ConnectionOpenClose[NUM_THREADS];
        for(int i = 0 ; i < NUM_THREADS; ++i) {
            runnables[i] = new ConnectionOpenClose(i, getConnection(), NUM_ITERATIONS);
            threads[i] = new Thread(runnables[i]);
            threads[i].start();
        }
        _log.info("Started " + NUM_THREADS + " threads");
        for(int i = 0 ; i < NUM_THREADS; ++i) {
            threads[i].join();
            if (runnables[i].getException() != null) {
                _log.error("Got exception on thread " + i, runnables[i].getException());
                throw runnables[i].getException();
            }
        }
    }

    private class ConnectionOpenClose implements Runnable {
        private final int _id;
        private final NevadoConnection _connection;
        private final int _iterations;
        private final Random _random = new Random();
        private volatile Exception _exception = null;
        private final StopWatch _stopWatch = new StopWatch();

        public ConnectionOpenClose(int id, NevadoConnection connection, int iterations) {
            _id = id;
            _connection = connection;
            _iterations = iterations;
        }

        public void run() {
            for(int i = 0 ; i < _iterations ; ++i) {
                try {
                    _stopWatch.reset();
                    _stopWatch.start();
                    _connection.stop();
                    _stopWatch.stop();
                    _log.debug("[" + _id + "] connection.stop() took " + _stopWatch.getTime() + " ms");
                    Thread.sleep(_random.nextInt(10));
                    _stopWatch.reset();
                    _stopWatch.start();
                    _connection.start();
                    _stopWatch.stop();
                    _log.debug("[" + _id + "] connection.start() took " + _stopWatch.getTime() + " ms");
                    Thread.sleep(_random.nextInt(10));
                }
                catch (Exception e) {
                    _exception = e;
                    break;
                }
            }
        }

        public Exception getException() {
            return _exception;
        }
    }
}
