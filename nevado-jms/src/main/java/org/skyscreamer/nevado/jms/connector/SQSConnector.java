package org.skyscreamer.nevado.jms.connector;

import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.JMSException;
import java.util.Collection;
import java.util.List;

/**
 * Interface for connecting to the underlying implementation of the messaging system
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public interface SQSConnector {
    void sendMessage(NevadoDestination destination, NevadoMessage outgoingMessage) throws JMSException;
    void sendMessages(NevadoDestination destination, List<NevadoMessage> outgoingMessages) throws JMSException;

    NevadoMessage receiveMessage(NevadoConnection connection, NevadoDestination destination, long timeoutMs)
        throws JMSException;
    void deleteMessage(NevadoMessage message) throws JMSException;
    void resetMessage(NevadoMessage message) throws JMSException;

    void test() throws JMSException;

    NevadoQueue createQueue(String queueName) throws JMSException;
    void deleteQueue(NevadoQueue queue) throws JMSException;
    Collection<NevadoQueue> listQueues(String temporaryQueuePrefix) throws JMSException;

    NevadoTopic createTopic(String tempTopicName) throws JMSException;
    void deleteTopic(NevadoTopic topic) throws JMSException;
    Collection<NevadoTopic> listTopics() throws JMSException;

    String subscribe(NevadoTopic topic, NevadoQueue topicEndpoint) throws JMSException;
    void unsubscribe(NevadoTopic topic) throws JMSException;
    void unsubscribeDurableQueueFromTopic(NevadoQueue queue);
}
