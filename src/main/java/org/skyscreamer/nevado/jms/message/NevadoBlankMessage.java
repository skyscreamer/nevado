package org.skyscreamer.nevado.jms.message;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/26/12
 * Time: 11:19 PM
 */
public class NevadoBlankMessage extends NevadoMessage implements Message {
    @Override
    public void internalClearBody() throws JMSException {
        // Do nothing
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NevadoBlankMessage that = (NevadoBlankMessage) o;

        if (_messageID != null ? !_messageID.equals(that._messageID) : that._messageID != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _messageID != null ? _messageID.hashCode() : 0;
    }
}
