package org.skyscreamer.nevado.jms.connector.mock;

import org.skyscreamer.nevado.jms.connector.AbstractSQSConnector;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;

import javax.jms.JMSException;
import java.util.*;

/**
 * Mock SQSConnector to test functionality without having to connect to AWS.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MockSQSConnector extends AbstractSQSConnector implements ResettableMock {
    private final Map<NevadoQueue, MockSQSQueue> _mockQueueMap = new HashMap<NevadoQueue, MockSQSQueue>();
    private final Map<NevadoTopic, Collection<MockSQSQueue>> _mockTopicMap = new HashMap<NevadoTopic, Collection<MockSQSQueue>>();

    public MockSQSConnector() {
        this(200);
    }

    public MockSQSConnector(long receiveCheckIntervalMs) {
        super(receiveCheckIntervalMs);
    }

    @Override
    protected void sendSNSMessage(NevadoTopic topic, String body) throws JMSException {
        checkTopicExists(topic);
        body = "{Message:\"" + body + "\"}";
        MockSQSMessage message = new MockSQSMessage(body);
        for(MockSQSQueue queue : _mockTopicMap.get(topic))
        {
            queue.sendMessage(body);
        }
    }

    @Override
    protected MockSQSQueue getSQSQueueImpl(NevadoQueue queue) throws JMSException {
        MockSQSQueue mockQueue;
        synchronized (_mockQueueMap) {
            mockQueue = _mockQueueMap.get(queue);
            if (mockQueue == null)
            {
                mockQueue = new MockSQSQueue(this, queue);
                _mockQueueMap.put(queue, mockQueue);
            }
        }
        return mockQueue;
    }

    @Override
    public void test() throws JMSException {
        // nop
    }

    @Override
    public Collection<NevadoQueue> listQueues(String temporaryQueuePrefix) throws JMSException {
        Collection<NevadoQueue> queues = new ArrayList<NevadoQueue>();
        for(NevadoQueue queue : _mockQueueMap.keySet())
        {
            if (queue.getName().startsWith(temporaryQueuePrefix))
            {
                queues.add(queue);
            }
        }
        return queues;
    }

    @Override
    public NevadoTopic createTopic(String topicName) throws JMSException {
        NevadoTopic nevadoTopic = new NevadoTopic(topicName);
        _mockTopicMap.put(nevadoTopic, new HashSet<MockSQSQueue>());
        return nevadoTopic;
    }

    @Override
    public void deleteTopic(NevadoTopic topic) throws JMSException {
        checkTopicExists(topic);
        _mockTopicMap.remove(topic);
    }

    @Override
    public Collection<NevadoTopic> listTopics() throws JMSException {
        return _mockTopicMap.keySet();
    }

    @Override
    public String subscribe(NevadoTopic topic, NevadoQueue topicEndpoint) throws JMSException {
        if (!_mockTopicMap.containsKey(topic))
        {
            createTopic(topic.getTopicName());
        }
        MockSQSQueue endpointQueue = getSQSQueueImpl(topicEndpoint);
        _mockTopicMap.get(topic).add(endpointQueue);
        return endpointQueue.getQueueARN();
    }

    @Override
    public void unsubscribe(NevadoTopic topic) throws JMSException {
        Collection<MockSQSQueue> topicEndpoints = _mockTopicMap.get(topic);
        if (topicEndpoints != null) {
            for(MockSQSQueue endpointQueue : topicEndpoints)
            {
                if (endpointQueue.getQueue().equals(topic.getTopicEndpoint()))
                {
                    topicEndpoints.remove(endpointQueue);
                    break;
                }
            }
        }
    }

    @Override
    public void reset() {
        Collection<MockSQSQueue> mockSQSQueues = buildSetOfAllMockQueues();
        for (MockSQSQueue mockSQSQueue : mockSQSQueues) {
            mockSQSQueue.reset();
        }
    }

    protected void removeQueue(NevadoQueue queue) {
        _mockQueueMap.remove(queue);
        for(NevadoTopic topic : _mockTopicMap.keySet())
        {
            Collection<MockSQSQueue> topicEndpoints = _mockTopicMap.get(topic);
            for(MockSQSQueue endpointQueue : topicEndpoints)
            {
                if (endpointQueue.getQueue().equals(topic.getTopicEndpoint()))
                {
                    topicEndpoints.remove(endpointQueue);
                    break;
                }
            }
        }
    }

    private void checkTopicExists(NevadoTopic topic) throws JMSException {
        if (!_mockTopicMap.containsKey(topic))
        {
            throw new JMSException("No such topic: " + topic);
        }
    }

    private Collection<MockSQSQueue> buildSetOfAllMockQueues(){
        Collection<MockSQSQueue> mockSQSQueues = new HashSet<MockSQSQueue>();

        mockSQSQueues.addAll(_mockQueueMap.values());

        Collection<Collection<MockSQSQueue>> allTopicQueueCollections = _mockTopicMap.values();
        for (Collection<MockSQSQueue> topicQueueCollection : allTopicQueueCollections) {
            mockSQSQueues.addAll(topicQueueCollection);
        }

        return mockSQSQueues;
    }
}
