package org.skyscreamer.nevado.jms.connector;

/**
 * Abstraction of an SQS message.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public interface SQSMessage {
    String getReceiptHandle();

    String getMessageBody();

    String getMessageId();
}
