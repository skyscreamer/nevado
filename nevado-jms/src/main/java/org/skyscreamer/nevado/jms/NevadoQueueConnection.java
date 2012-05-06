package org.skyscreamer.nevado.jms;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Nevado implementation of QueueConnection
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoQueueConnection extends NevadoConnection implements QueueConnection {
    public NevadoQueueConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        super(awsAccessKey, awsSecretKey);
    }

    @Override
    public synchronized NevadoQueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        _inUse = true;
        NevadoQueueSession nevadoSession = new NevadoQueueSession(this, transacted, acknowledgeMode);
        initializeSession(nevadoSession);
        return nevadoSession;
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Queue queue, String s, ServerSessionPool serverSessionPool,
                                                       int i)
            throws JMSException
    {
        checkClosed();
        _inUse = true;
        return null;  // TODO
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1,
                                                              ServerSessionPool serverSessionPool, int i)
            throws JMSException
    {
        throw new IllegalStateException("Can't create a durable consumer from a QueueConnection");
    }
}
