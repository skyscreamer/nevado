package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.util.BackoffSleeper;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * Asynchronous processor for consumers with registered message listeners
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoSessionExecutor implements Runnable {
    private final Log _log = LogFactory.getLog(getClass());
    private final NevadoSession _session;
    private final Object _runningLock = new Object();
    private volatile boolean _closed = false;
    private final BackoffSleeper _sleeper = new BackoffSleeper(10, 15000, 2.0);
    private Thread runner;

    protected NevadoSessionExecutor(NevadoSession session) {
        _session = session;
    }

    public void run() {
        while(!_closed) {
            boolean messageProcessed = false;
            for(NevadoMessageConsumer consumer : _session.getMessageConsumers()) {
                if (_closed)
                {
                    break;
                }
                if (consumer.getMessageListener() != null) {
                    try {
                        if (consumer.processAsyncMessage()) {
                            messageProcessed = true;
                        }
                    } catch (JMSException e) {
                        _log.error("Unable to process message for consumer on " + consumer.getDestination(), e);
                        ExceptionListener exceptionListener = _session.getConnection().getExceptionListener();
                        if (exceptionListener != null)
                        {
                            exceptionListener.onException(e);
                        }
                    }
                }
            }
            if (!_closed) {
                if (messageProcessed == true)
                {
                    // If we're getting messages tell the back-off sleeper
                    _sleeper.reset();
                }
                _sleeper.sleep();
            }
        }
        synchronized (_runningLock) {
            _runningLock.notify();
        }
    }

    synchronized void start() {
        if (!_closed && runner == null) {
            runner = new Thread(this);
            //runner.setPriority(Thread.MAX_PRIORITY);
            //runner.setDaemon(true);
            runner.start();
        }
    }

    synchronized void stop() throws InterruptedException {
        if (!_closed) {
            _closed = true;
            synchronized (_runningLock) {
                _runningLock.wait();
            }
        }
    }

    public boolean isRunning() {
        return runner != null && !_closed;
    }
}
