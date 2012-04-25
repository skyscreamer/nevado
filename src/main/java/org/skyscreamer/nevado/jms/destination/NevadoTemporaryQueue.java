package org.skyscreamer.nevado.jms.destination;

import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.JMSException;
import javax.jms.TemporaryQueue;

/**
 * Nevado implementation of a temporary queue
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoTemporaryQueue extends NevadoQueue implements TemporaryQueue {
    private final transient NevadoConnection _connection;

    public NevadoTemporaryQueue(NevadoConnection connection, NevadoQueue queue) throws JMSException {
        super(queue);
        _connection = connection;
    }

    public synchronized void delete() throws JMSException {
        if (_connection != null) {
            _connection.deleteTemporaryQueue(this);
        }
    }
}
