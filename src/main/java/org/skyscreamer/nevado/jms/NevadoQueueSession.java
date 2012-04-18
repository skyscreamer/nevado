package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Nevado implementation of QueueSession
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoQueueSession extends NevadoSession implements QueueSession {
    protected NevadoQueueSession(NevadoConnection connection, boolean transacted, int acknowledgeMode) {
        super(connection, transacted, acknowledgeMode);
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException
    {
        checkClosed();
        return createConsumer(queue);
    }

    public QueueReceiver createReceiver(Queue queue, String selector) throws JMSException
    {
        checkClosed();
        return createConsumer(queue, selector);
    }

    public QueueSender createSender(Queue queue) throws JMSException
    {
        checkClosed();
        return createProducer(queue);
    }

    // Delegate/check Destination methods to make sure we're not handling topics
    @Override
    public NevadoMessageProducer createProducer(Destination destination) throws JMSException {
        checkIsQueue(destination);
        return super.createProducer(destination);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination) throws JMSException {
        checkIsQueue(destination);
        return super.createConsumer(destination);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination, String selector) throws JMSException {
        checkIsQueue(destination);
        return super.createConsumer(destination, selector);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination, String selector, boolean noLocal) throws JMSException {
        checkIsQueue(destination);
        return super.createConsumer(destination, selector, noLocal);
    }

    @Override
    public void sendMessage(NevadoDestination destination, NevadoMessage message) throws JMSException {
        checkIsQueue(destination);
        super.sendMessage(destination, message);
    }

    @Override
    public Message receiveMessage(NevadoDestination destination, long timeoutMs) throws JMSException {
        checkIsQueue(destination);
        return super.receiveMessage(destination, timeoutMs);
    }

    private void checkIsQueue(Destination destination) throws IllegalStateException {
        if (!(destination instanceof Queue))
        {
            throw new IllegalStateException("QueueSession does not handle destinations of type "
                    + destination.getClass().getName());
        }
    }

    // Override topic methods to throw IllegalStateException
    @Override
    public NevadoTopic createTopic(String s) throws JMSException {
        throw new IllegalStateException("QueueSession will not perform topic operations");
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        throw new IllegalStateException("QueueSession will not perform topic operations");
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b) throws JMSException {
        throw new IllegalStateException("QueueSession will not perform topic operations");
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s) throws JMSException {
        throw new IllegalStateException("QueueSession will not perform topic operations");
    }

    @Override
    public void unsubscribe(String s) throws JMSException {
        throw new IllegalStateException("QueueSession will not perform topic operations");
    }
}
