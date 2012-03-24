package org.skyscreamer.nevado.jms.message;

import com.xerox.amazonws.sqs2.Message;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public class NevadoTextMessage extends NevadoMessage implements TextMessage {
    public void setText(String text) throws JMSException {
        checkReadOnlyBody();
        setBody(text);
    }

    public String getText() throws JMSException {
        return getBody();
    }
}
