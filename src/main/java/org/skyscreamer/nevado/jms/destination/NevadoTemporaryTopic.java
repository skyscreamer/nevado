package org.skyscreamer.nevado.jms.destination;

import org.skyscreamer.nevado.jms.NevadoConnection;

import javax.jms.JMSException;
import javax.jms.TemporaryTopic;

/**
 * TODO - Add description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoTemporaryTopic extends NevadoTopic implements TemporaryTopic {
    private transient NevadoConnection _connection;

    public NevadoTemporaryTopic(NevadoConnection connection, NevadoTopic topic) {
        super(topic);
        _connection = connection;
    }

    public synchronized void delete() throws JMSException {
        if (_connection != null) {
            _connection.deleteTemporaryTopic(this);
        }
    }
}
