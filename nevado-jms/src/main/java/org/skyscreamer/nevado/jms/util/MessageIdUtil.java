package org.skyscreamer.nevado.jms.util;

import java.util.UUID;

/**
 * Generate nevado-specific message ID's when SQS message ID's aren't sufficient
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageIdUtil {
    public static String createMessageId() {
        return "ID:nevado-" + UUID.randomUUID().toString();
    }
}
