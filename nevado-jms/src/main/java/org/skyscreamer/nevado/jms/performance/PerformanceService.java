package org.skyscreamer.nevado.jms.performance;

import org.apache.commons.lang.time.StopWatch;

import javax.jms.*;

/**
 * Runs a sample of messages to test send/receive performance
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PerformanceService {
    public PerformanceResult runSample(Connection connection, int numMessages, long messageSendDelayMs)
            throws JMSException, InterruptedException
    {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createTemporaryQueue();
        StopWatch swSend = new StopWatch();
        swSend.start();
        sendMessages(numMessages, session, queue, messageSendDelayMs);
        swSend.stop();

        Integer[] msgCounter = createMessageCounter(numMessages);
        StopWatch swReceive = new StopWatch();
        swReceive.start();
        int numOutOfOrder = receiveMessages(numMessages, session, queue, msgCounter);
        swReceive.stop();
        int numMessagesReceived = countAllMessages(msgCounter);
        int numDupMessages = countDupMessages(msgCounter);
        int numMissedMessages = countMissedMessages(msgCounter);

        PerformanceResult result = new PerformanceResult(numMessages, numMessagesReceived, numDupMessages,
                numMissedMessages, numOutOfOrder, swSend.getTime(), swReceive.getTime());
        return result;
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
            int id = (Integer)((ObjectMessage)msg).getObject();
            if (id <= lastMsgId)
            {
                ++outOfOrderCount;
            }
            ++msgCounter[id];
            lastMsgId = id;
        }
        return outOfOrderCount;
    }

    private void sendMessages(int numMessages, Session session, Queue queue, long messageSendDelayMs)
            throws JMSException, InterruptedException
    {
        MessageProducer producer = session.createProducer(queue);
        for(int i = 0 ; i < numMessages ; ++i)
        {
            ObjectMessage msg = session.createObjectMessage(i);
            producer.send(msg);
            Thread.sleep(messageSendDelayMs);
        }
    }
}
