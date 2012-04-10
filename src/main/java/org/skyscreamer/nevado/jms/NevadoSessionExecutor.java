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
        synchronized (_runningLock) {
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
                        _sleeper.reset();
                    }
                    _sleeper.sleep();
                }
            }
        }
    }

    synchronized void start() {
        if (runner == null) {
            runner = new Thread(this);
            //runner.setPriority(Thread.MAX_PRIORITY);
            //runner.setDaemon(true);
            runner.start();
        }
    }

    public void stop() {
        _closed = true;
        synchronized (_runningLock) {
            // This should freeze until the thread exits
        }
        runner = null;
    }

    public boolean isRunning() {
        return runner != null && !_closed;
    }
}
