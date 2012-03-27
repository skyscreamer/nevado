package org.skyscreamer.nevado.jms.message;

import org.skyscreamer.nevado.jms.NevadoDestination;
import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.*;
import javax.jms.Message;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: cpage
 * Date: 3/21/12
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NevadoMessage extends AbstractMessage implements Message {
    private transient NevadoSession _nevadoSession;
    private transient NevadoDestination _nevadoDestination;

    public NevadoMessage() {}

    protected NevadoMessage(Message message) throws JMSException {
        setJMSMessageID(message.getJMSMessageID());
        setJMSCorrelationID(message.getJMSCorrelationID());
        setJMSReplyTo(NevadoDestination.getInstance(message.getJMSReplyTo()));
        setJMSDestination(NevadoDestination.getInstance(message.getJMSDestination()));
        setJMSDeliveryMode(message.getJMSDeliveryMode());
        setJMSRedelivered(message.getJMSRedelivered());
        setJMSType(message.getJMSType());
        setJMSExpiration(message.getJMSExpiration());
        setJMSPriority(message.getJMSPriority());
        setJMSTimestamp(message.getJMSTimestamp());
        for (Enumeration propertyNames = message.getPropertyNames(); propertyNames.hasMoreElements();) {
            String name = propertyNames.nextElement().toString();
            Object obj = message.getObjectProperty(name);
            setObjectProperty(name, obj);
        }
    }

    public NevadoSession getNevadoSession() {
        return _nevadoSession;
    }

    public void setNevadoSession(NevadoSession nevadoSession) {
        _nevadoSession = nevadoSession;
    }

    public NevadoDestination getNevadoDestination() {
        return _nevadoDestination;
    }

    public void setNevadoDestination(NevadoDestination nevadoDestination) {
        _nevadoDestination = nevadoDestination;
    }

    public boolean nevadoPropertyExists(NevadoProperty property) throws JMSException {
        return super.propertyExists(property + "");
    }

    public String getNevadoStringProperty(NevadoProperty property) throws JMSException {
        return super.getStringProperty(property + "");
    }

    public void setNevadoStringProperty(NevadoProperty nevadoProperty, String value) throws JMSException {
        super.setStringProperty(nevadoProperty + "", value);
    }

    public boolean getNevadoBooleanProperty(NevadoProperty nevadoProperty) throws JMSException {
        return super.getBooleanProperty(nevadoProperty + "");
    }

    public void setNevadoBooleanProperty(NevadoProperty nevadoProperty, boolean value) throws JMSException {
        super.setBooleanProperty(nevadoProperty + "", value);
    }

    public void acknowledge() throws JMSException {
        _nevadoSession.deleteMessage(this);
    }

    public static NevadoMessage getInstance(Message message) throws JMSException {
        NevadoMessage nevadoMessage = null;

        if (message != null) {
            if (message instanceof NevadoMessage) {
                nevadoMessage = (NevadoMessage) message;
            }
            else {
                if (message instanceof StreamMessage) {
                    // Create new NevadoStreamMessage - TODO
                }
                else if (message instanceof MapMessage) {
                    nevadoMessage = new NevadoMapMessage((MapMessage)message);
                }
                else if (message instanceof TextMessage) {
                    nevadoMessage = new NevadoTextMessage((TextMessage)message);
                }
                else if (message instanceof ObjectMessage) {
                    nevadoMessage = new NevadoObjectMessage((ObjectMessage)message);
                }
                else if (message instanceof BytesMessage) {
                    // Create new NevadoBytesMessage - TODO
                }
                else {
                    throw new UnsupportedOperationException("Unable to parse message of type: " + message.getClass().getName());
                }
            }
        }

        return nevadoMessage;
    }
}
