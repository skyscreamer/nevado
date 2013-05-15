package org.skyscreamer.nevado.jms.connector.typica;

import com.xerox.amazonws.sqs2.Message;
import org.skyscreamer.nevado.jms.connector.SQSMessage;

/**
 * Typica-specific version of an SQSMessage
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
@Deprecated
class TypicaSQSMessage implements SQSMessage {
    private final Message _message;

    public TypicaSQSMessage(Message message) {
        _message = message;
    }

    @Override
    public String getReceiptHandle() {
        return _message.getReceiptHandle();
    }

    @Override
    public String getMessageBody() {
        return _message.getMessageBody();
    }

    @Override
    public String getMessageId() {
        return _message.getMessageId();
    }
}
