package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.connector.NevadoConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Nevado implementation of a queue browser
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoQueueBrowser implements QueueBrowser {
    private final NevadoSession _session;
    private final NevadoQueue _queue;
    private int NUM_MESSAGES_TO_BROWSE = 10;

    public NevadoQueueBrowser(NevadoSession session, NevadoQueue queue) {
        _session = session;
        _queue = queue;
    }

    @Override
    public NevadoQueue getQueue() throws JMSException {
        return _queue;
    }

    @Override
    public String getMessageSelector() throws JMSException {
        return null;
    }

    @Override
    public Enumeration getEnumeration() throws JMSException {
        NevadoConnector sqsConnector = _session.getConnection().getSQSConnector();
        List<NevadoMessage> messages = sqsConnector.browseMessages(_queue, NUM_MESSAGES_TO_BROWSE);
        return new Vector<NevadoMessage>(messages).elements();
    }

    @Override
    public void close() throws JMSException {
        // Do nothing
    }
}
