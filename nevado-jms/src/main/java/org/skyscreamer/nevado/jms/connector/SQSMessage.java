package org.skyscreamer.nevado.jms.connector;

import java.util.Map;

/**
 * Abstraction of an SQS message.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public interface SQSMessage {
    String getReceiptHandle();

    String getMessageBody();

    String getMessageId();

    Map<String, String> getAttributes();
}
