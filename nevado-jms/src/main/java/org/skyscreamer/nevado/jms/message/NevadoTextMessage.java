package org.skyscreamer.nevado.jms.message;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public class NevadoTextMessage extends NevadoMessage implements TextMessage {
    private String _body;

    public NevadoTextMessage() {}

    protected NevadoTextMessage(TextMessage message) throws JMSException {
        super(message);
        _body = message.getText();
    }

    public void setText(String text) throws JMSException {
        checkReadOnlyBody();
        _body = text;
    }

    public String getText() throws JMSException {
        return _body;
    }

    @Override
    public void internalClearBody() throws JMSException {
        _body = null;
    }

    @Override
    public String toString() {
        return _body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NevadoTextMessage that = (NevadoTextMessage) o;

        if (_messageID != null ? !_messageID.equals(that._messageID) : that._messageID != null) return false;
        if (_body != null ? !_body.equals(that._body) : that._body != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_messageID).append(_body).toHashCode();
    }
}
