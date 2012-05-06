package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Nevado implementation of TopicSession
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoTopicSession extends NevadoSession implements TopicSession {
    protected NevadoTopicSession(NevadoConnection connection, boolean transacted, int acknowledgeMode) {
        super(connection, transacted, acknowledgeMode);
    }

    @Override
    public TopicSubscriber createSubscriber(Topic topic) throws JMSException
    {
        checkClosed();
        return super.createConsumer(topic);
    }

    @Override
    public TopicSubscriber createSubscriber(Topic topic, String selector, boolean noLocal) throws JMSException
    {
        checkClosed();
        return super.createConsumer(topic, selector, noLocal);
    }

    @Override
    public TopicPublisher createPublisher(Topic topic) throws JMSException
    {
        checkClosed();
        return super.createProducer(topic);
    }

    // Delegate/check Destination methods to make sure we're not handling queues
    @Override
    public NevadoMessageProducer createProducer(Destination destination) throws JMSException {
        checkIsTopic(destination);
        return super.createProducer(destination);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination) throws JMSException {
        checkIsTopic(destination);
        return super.createConsumer(destination);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination, String selector) throws JMSException {
        checkIsTopic(destination);
        return super.createConsumer(destination, selector);
    }

    @Override
    public NevadoMessageConsumer createConsumer(Destination destination, String selector, boolean noLocal) throws JMSException {
        checkIsTopic(destination);
        return super.createConsumer(destination, selector, noLocal);
    }

    private void checkIsTopic(Destination destination) throws IllegalStateException {
        if (!(destination instanceof Topic))
        {
            throw new IllegalStateException("TopicSession does not handle destinations of type "
                    + destination.getClass().getName());
        }
    }

    // Override topic methods to throw IllegalStateException
    @Override
    public NevadoQueueBrowser createBrowser(Queue queue) throws JMSException {
        throw new IllegalStateException("TopicSession will not perform queue operations");
    }

    @Override
    public NevadoQueueBrowser createBrowser(Queue queue, String s) throws JMSException {
        throw new IllegalStateException("TopicSession will not perform queue operations");
    }

    @Override
    public NevadoTemporaryQueue createTemporaryQueue() throws JMSException {
        throw new IllegalStateException("TopicSession will not perform queue operations");
    }

    @Override
    public NevadoQueue createQueue(String s) throws JMSException {
        throw new IllegalStateException("TopicSession will not perform queue operations");
    }
}
