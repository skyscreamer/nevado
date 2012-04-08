package org.skyscreamer.nevado.jms.destination;

import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.JMSException;
import javax.jms.TemporaryQueue;

/**
 * TODO - Description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoTemporaryQueue extends NevadoQueue implements TemporaryQueue {
    private transient NevadoSession _session;

    public NevadoTemporaryQueue(NevadoSession session, NevadoQueue queue) {
        super(queue);
        _session = session;
    }

    public synchronized void delete() throws JMSException {
        if (_session != null) {
            _session.deleteTemporaryQueue(this);
        }
    }
}
