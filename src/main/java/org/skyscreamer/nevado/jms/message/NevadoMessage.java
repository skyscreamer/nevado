package org.skyscreamer.nevado.jms.message;

import org.skyscreamer.nevado.jms.NevadoDestination;
import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.*;
import javax.jms.Message;

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

    public String getNevadoStringProperty(NevadoProperty property) throws JMSException {
        return super.getStringProperty(property + "");
    }

    public void setNevadoStringProperty(NevadoProperty nevadoProperty, String value) throws JMSException {
        super.setStringProperty(nevadoProperty + "", value);
    }

    public void acknowledge() throws JMSException {
        _nevadoSession.deleteMessage(this);
    }

    public static NevadoMessage getInstance(Message message) {
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
                    // Create new NevadoMapMessage - TODO
                }
                else if (message instanceof TextMessage) {
                    // Create new NevadoTextMessage - TODO
                }
                else if (message instanceof ObjectMessage) {
                    // Create new NevadoTopic - TODO
                }
                else if (message instanceof BytesMessage) {
                    // Create new NevadoTopic - TODO
                }
                else {
                    throw new UnsupportedOperationException("Unable to parse message of type: " + message.getClass().getName());
                }
            }
        }

        return nevadoMessage;
    }
}
