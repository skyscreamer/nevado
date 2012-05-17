package org.skyscreamer.nevado.jms.connector;

import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.JMSException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock SQSConnector to test functionality without having to connect to AWS.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MockSQSConnector extends AbstractSQSConnector {
    private final Map<NevadoQueue, List<String>> _mockQueueMap = new HashMap<NevadoQueue, List<String>>();

    public MockSQSConnector(long receiveCheckIntervalMs) {
        super(receiveCheckIntervalMs);
    }

    @Override
    protected NevadoMessage convertSqsMessage(NevadoDestination destination, SQSMessage sqsMessage, boolean b) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void sendSNSMessage(NevadoTopic topic, String serializedMessage) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected SQSQueue getSQSQueue(NevadoDestination destination) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /*
    @Override
    protected String sendSQSMessage(NevadoQueue queue, String serializedMessage) throws JMSException {
        List<String> _mockQueue = getMockQueue(queue);
        synchronized (_mockQueue) {
            _mockQueue.add(0, serializedMessage);
        }
        return null;  // TODO
    }

    private List<String> getMockQueue(NevadoQueue queue) {
        List<String> mockQueue;
        synchronized (_mockQueueMap) {
            mockQueue = _mockQueueMap.get(queue);
            if (mockQueue == null)
            {
                mockQueue = new LinkedList<String>();
                _mockQueueMap.put(queue, mockQueue);
            }
        }
        return mockQueue;
    }
    */

    @Override
    public void test() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<NevadoQueue> listQueues(String temporaryQueuePrefix) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NevadoTopic createTopic(String tempTopicName) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteTopic(NevadoTopic topic) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<NevadoTopic> listTopics() throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String subscribe(NevadoTopic topic, NevadoQueue topicEndpoint) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unsubscribe(NevadoTopic topic) throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unsubscribeDurableQueueFromTopic(NevadoQueue queue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
