package org.skyscreamer.nevado.jms.performance;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;

/**
 * Runs a sample of messages to analyze send/receive performance
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PerformanceService {
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * <p>Run a sample of messages, testing for performance.</p>
     * <code>Total # of messages = numMessages X numThreads</code>
     *
     * @param connection Connection to use for creating temporary queues, and sending/receiving messages
     * @param numMessages Number of messages to send on each thread
     * @param messageSize Size of message payload (max 48k)
     * @param numThreads Number of threads to create for sending and receiving
     * @param messageSendDelayMs Delay time between sending each message.  Added to total send time.  Recommended 0.
     * @return A result object containing the details of the performance run
     * @throws JMSException A JMS exception was encountered during the run
     * @throws InterruptedException One or more threads was interrupted during the run
     */
    public PerformanceResult runSample(Connection connection, int numMessages, int messageSize,
                                       int numThreads, long messageSendDelayMs)
            throws JMSException, InterruptedException
    {
        Session[] sessions = new Session[numThreads];
        Queue[] queues = new Queue[numThreads];
        initializeSessionsAndQueues(connection, sessions, queues, numThreads);
        long sendTime = sendMessages(numMessages, messageSize, numThreads, messageSendDelayMs, sessions, queues);

        PerformanceTally tally = new PerformanceTally();
        long receiveTime = receiveMessages(numMessages, numThreads, sessions, queues, tally);

        PerformanceResult result = new PerformanceResult(numMessages * numThreads, tally, messageSize, numThreads,
                sendTime, receiveTime);
        return result;
    }

    private long receiveMessages(int numMessages, int numThreads, Session[] sessions, Queue[] queues,
                                 PerformanceTally totalTally) throws JMSException, InterruptedException
    {
        StopWatch swReceive = new StopWatch();
        swReceive.start();
        PerformanceTally[] tallies = new PerformanceTally[numThreads];
        Thread[] threads = new Thread[numThreads];
        for(int i = 0 ; i < numThreads ; ++i) {
            tallies[i] = new PerformanceTally();
            threads[i] = createReceiverThread(tallies[i], numMessages, sessions[i], queues[i]);
            threads[i].start();
        }
        for(int i = 0 ; i < numThreads ; ++i) {
            threads[i].join();
        }
        swReceive.stop();
        for(int i = 0 ; i < numThreads ; ++i)
        {
            totalTally.add(tallies[i]);
        }
        return swReceive.getTime();
    }

    private Thread createReceiverThread(final PerformanceTally tally, final int numMessages, final Session session,
                                        final Queue queue)
            throws JMSException
    {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Integer[] msgCounter = createMessageCounter(numMessages);
                int numOutOfOrder = 0;
                try {
                    numOutOfOrder = receiveMessages(numMessages, session, queue, msgCounter);
                    tally.setNumOutOfOrder(numOutOfOrder);
                    tally.setNumMessagesReceived(countAllMessages(msgCounter));
                    tally.setNumDupMessages(countDupMessages(msgCounter));
                    tally.setNumMissedMessages(countMissedMessages(msgCounter));
                } catch (JMSException e) {
                    _log.error("Error receiving message", e);
                }
            }
        });
    }

    private void initializeSessionsAndQueues(Connection connection, Session[] sessions, Queue[] queues, int numThreads)
            throws JMSException
    {
        for (int i = 0; i < numThreads; ++i) {
            sessions[i] = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            queues[i] = sessions[i].createTemporaryQueue();
        }
    }

    private long sendMessages(int numMessages, int messageSize, int numThreads, long messageSendDelayMs,
                              Session[] sessions, Queue[] queues) throws InterruptedException
    {
        StopWatch swSend = new StopWatch();
        swSend.start();
        Thread[] threads = new Thread[numThreads];
        for(int i = 0 ; i < numThreads ; ++i)
        {
            threads[i] = createSenderThread(numMessages, messageSize, messageSendDelayMs, sessions[i], queues[i]);
            threads[i].start();
        }
        for(int i = 0 ; i < numThreads ; ++i)
        {
            threads[i].join();
        }
        swSend.stop();
        return swSend.getTime();
    }

    private Thread createSenderThread(final int numMessages, final int messageSize, final long messageSendDelayMs, final Session session, final Queue queue) {
        return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendMessages(numMessages, messageSize, session, queue, messageSendDelayMs);
                        } catch (Exception e) {
                            _log.error("Error sending messages", e);
                        }
                    }
                });
    }

    private int countAllMessages(Integer[] msgCounter) {
        int count = 0;
        for(int i = 0 ; i < msgCounter.length ; ++i)
        {
            count += msgCounter[i];
        }
        return count;
    }

    private int countMissedMessages(Integer[] msgCounter) {
        int count = 0;
        for(int i = 0 ; i < msgCounter.length ; ++i)
        {
            if (msgCounter[i] == 0)
            {
                ++count;
            }
        }
        return count;
    }

    private int countDupMessages(Integer[] msgCounter) {
        int count = 0;
        for(int i = 0 ; i < msgCounter.length ; ++i)
        {
            if (msgCounter[i] > 1)
            {
                count += msgCounter[i] - 1;
            }
        }
        return count;
    }

    private Integer[] createMessageCounter(int numMessages) {
        Integer[] msgCounter = new Integer[numMessages];
        for(int i = 0 ; i < numMessages ; ++i)
        {
            msgCounter[i] = 0;
        }
        return msgCounter;
    }

    private int receiveMessages(int numMessages, Session session, Queue queue, Integer[] msgCounter) throws JMSException {
        // Initialize variables
        int lastMsgId = -1;
        int outOfOrderCount = 0;
        Message msg;

        // Receive messages
        MessageConsumer consumer = session.createConsumer(queue);
        while((msg = consumer.receive(100)) != null) {
            PerformancePayload payload = (PerformancePayload)((ObjectMessage)msg).getObject();
            if (payload.getId() <= lastMsgId)
            {
                ++outOfOrderCount;
            }
            ++msgCounter[payload.getId()];
            lastMsgId = payload.getId();
        }
        return outOfOrderCount;
    }

    private void sendMessages(int numMessages, int messageSize, Session session, Queue queue, long messageSendDelayMs)
            throws JMSException, InterruptedException
    {
        MessageProducer producer = session.createProducer(queue);
        for(int i = 0 ; i < numMessages ; ++i)
        {
            ObjectMessage msg = session.createObjectMessage(new PerformancePayload(i, messageSize));
            producer.send(msg);
            Thread.sleep(messageSendDelayMs);
        }
    }
}
