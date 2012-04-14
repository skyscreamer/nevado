package org.skyscreamer.nevado.jms.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.*;
import java.util.concurrent.*;

/**
 * Test MessageListener.  Listens for messages, and adds them to an array for later review.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TestMessageListener implements MessageListener {
    private final Log _log = LogFactory.getLog(getClass());
    private final BlockingQueue<Message> _messages = new LinkedBlockingQueue<Message>();

    public void onMessage(Message message) {
        _messages.add(message);
        try {
            message.acknowledge();
        } catch (JMSException e) {
            _log.error("Unable to acknowledge message", e);
            throw new RuntimeException(e);
        }
    }

    public NevadoMessage getMessage(long waitMs) {
        NevadoMessage message;
        try {
            message = (NevadoMessage)_messages.poll(waitMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // No message for you!
            message = null;
        }
        return message;
    }

    public boolean isEmpty()
    {
        return _messages.isEmpty();
    }
}
