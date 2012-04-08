package org.skyscreamer.nevado.jms.connector;

import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.JMSException;
import java.util.Collection;

/**
 * Interface for connecting to the underlying implementation of the messaging system
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public interface NevadoConnector {
    void sendMessage(NevadoDestination destination, NevadoMessage message, boolean disableMessageID,
        boolean disableTimestamp) throws JMSException;
    NevadoMessage receiveMessage(NevadoConnection connection, NevadoDestination destination, long timeoutMs)
        throws JMSException;
    void deleteMessage(NevadoMessage message) throws JMSException;
    void test() throws JMSException;

    NevadoQueue createQueue(String queueName) throws JMSException;
    void deleteQueue(NevadoQueue queue) throws JMSException;
    Collection<NevadoQueue> listQueues(String temporaryQueuePrefix) throws JMSException;
}
