package org.skyscreamer.nevado.jms.destination;

import org.skyscreamer.nevado.jms.resource.NevadoReferencableFactory;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import java.io.Serializable;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/24/12
 * Time: 10:22 AM
 */
public abstract class NevadoDestination implements Destination, Serializable, Referenceable {
    public static final String JNDI_DESTINATION_NAME = "name";

    private final String _name;
    private transient boolean _deleted;

    protected NevadoDestination(String name) {
        _name = name;
    }

    protected NevadoDestination(NevadoDestination destination) {
        _name = destination._name;
    }

    protected NevadoDestination(URL sqsURL) {
        if (sqsURL == null) {
            throw new NullPointerException("Null URL");
        }
        _name = parseQueueName(sqsURL.getPath());
    }

    private String parseQueueName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex <= 0 || (lastSlashIndex + 1) >= path.length())
        {
            throw new IllegalArgumentException("Can't parse queue name from invalid path: " + path);
        }
        return path.substring(lastSlashIndex + 1);
    }

    public String getName() {
        return _name;
    }

    public boolean isDeleted() {
        return _deleted;
    }

    public void setDeleted(boolean deleted) {
        _deleted = deleted;
    }

    public static NevadoDestination getInstance(Destination destination) throws JMSException {
        NevadoDestination nevadoDestination = null;

        if (destination != null) {
            if (destination instanceof NevadoDestination) {
                nevadoDestination = (NevadoDestination) destination;
            }
            else {
                if (destination instanceof TemporaryQueue) {
                    throw new IllegalStateException("TemporaryQueues cannot be copied");
                }
                else if (destination instanceof TemporaryTopic) {
                    throw new IllegalStateException("TemporaryDestinations cannot be copied");
                }
                else if (destination instanceof Queue) {
                    nevadoDestination = new NevadoQueue((Queue)destination);
                }
                else if (destination instanceof Topic) {
                    nevadoDestination = new NevadoTopic((Topic)destination);
                }
            }
        }

        return nevadoDestination;
    }

    @Override
    public Reference getReference() throws NamingException {
        Reference reference = new Reference(getClass().getName(),
                new StringRefAddr(JNDI_DESTINATION_NAME, _name),
                NevadoReferencableFactory.class.getName(), null);
        return reference;
    }

    @Override
    public String toString() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ||
                (!(this instanceof NevadoTopic && o instanceof NevadoTopic)
                        && !(this instanceof NevadoDestination && o instanceof NevadoDestination)))
            return false;

        NevadoDestination that = (NevadoDestination) o;

        if (_name != null ? !_name.equals(that._name) : that._name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _name != null ? _name.hashCode() : 0;
    }
}
