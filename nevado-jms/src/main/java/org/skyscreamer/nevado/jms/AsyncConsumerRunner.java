package org.skyscreamer.nevado.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.util.BackoffSleeper;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous processor for consumers with registered message listeners
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class  AsyncConsumerRunner implements Runnable {

    private static final AtomicInteger THREAD_NUM_COUNTER = new AtomicInteger(1);

    private final Log _log = LogFactory.getLog(getClass());
    private final Connection _connection;
    private final Set<NevadoMessageConsumer> _asyncConsumers = new CopyOnWriteArraySet<NevadoMessageConsumer>();
    private volatile boolean _running = false;
    private final BackoffSleeper _sleeper;
    private Thread runner;

    protected AsyncConsumerRunner(NevadoConnection connection) {
        _connection = connection;
        _sleeper = new BackoffSleeper(50, connection.getMaxPollWaitMs(), 1.5);
    }

    public void run() {
        _log.debug("Starting async loop");
        RUN_LOOP: while(_running) {
            _log.debug("Running async loop");
            boolean messageProcessed = false;
            for(NevadoMessageConsumer consumer : _asyncConsumers)
            {
                messageProcessed = processMessage(consumer) || messageProcessed;
                if (!_running) { break RUN_LOOP; }
            }
            if (messageProcessed)
            {
                // If we're getting messages tell the back-off sleeper
                _sleeper.reset();
            }
            _log.debug("Sleeping async loop");
            try {
                _sleeper.sleep();
            } catch (InterruptedException e) {
                _log.info("Loop interrupted");
                _running = false;
                Thread.currentThread().interrupt();
            }
        }
        _log.debug("Exiting async loop");
    }

    public void addAsyncConsumer(NevadoMessageConsumer asyncConsumer)
    {
        _asyncConsumers.add(asyncConsumer);
    }

    public void removeAsyncConsumer(NevadoMessageConsumer asyncConsumer)
    {
        _asyncConsumers.remove(asyncConsumer);
    }

    public int numAsyncConsumers()
    {
        return _asyncConsumers.size();
    }

    private boolean processMessage(NevadoMessageConsumer consumer) {
        boolean messageProcessed = false;
        if (consumer.getMessageListener() != null) {
            try {
                if (consumer.processAsyncMessage()) {
                    messageProcessed = true;
                }
            } catch (Throwable t) {
                String errorMessage = "Unable to process message for consumer on " + consumer.getDestination();
                _log.error(errorMessage, t);
                ExceptionListener exceptionListener = null;
                try {
                    exceptionListener = _connection.getExceptionListener();
                } catch (JMSException e1) {
                    _log.error("Unable to retrieve exception listener from connection", e1);
                }
                if (exceptionListener != null)
                {
                    JMSException e = (t instanceof JMSException) ? (JMSException)t
                            : new JMSException(errorMessage + ": " + t.getMessage());
                    exceptionListener.onException(e);
                }
            }
        }
        return messageProcessed;
    }

    /**
     * Checks that consumers actually have active message listeners
     *
     * When no one async message listener is registered - no need to create separate tread
     * which will create redundant requests to SQS
     *
     * Once async listener will be registered - AsyncConsumerRunner will be started
     *
     * {@link NevadoMessageConsumer#setMessageListener(javax.jms.MessageListener)}
     */
    private boolean consumersHaveAsyncMessageListeners(){

      for(NevadoMessageConsumer consumer : _asyncConsumers)
      {
        if(consumer.getMessageListener()!=null){
          return true;
        }
      }

      return false;
    }

    synchronized void start() {

        if(!consumersHaveAsyncMessageListeners()){
          return;
        }

        if (!_running) {
            runner = new Thread(this);
            //set more meaningful thread name
            int threadNum = THREAD_NUM_COUNTER.getAndIncrement();
            runner.setName("AsyncConsumerRunner Thread ["+threadNum+"]");
            //runner.setPriority(Thread.MAX_PRIORITY);
            //runner.setDaemon(true);
            runner.start();
            _running = true;
        }

    }

    synchronized void stop() throws InterruptedException {
        if (_running) {
            _running = false;
            if (runner != Thread.currentThread()) {
                _sleeper.stopSleeping();
                runner.join();
            }
        }
    }
}
