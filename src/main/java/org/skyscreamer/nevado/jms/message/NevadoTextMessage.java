package org.skyscreamer.nevado.jms.message;

import com.xerox.amazonws.sqs2.Message;

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
}
