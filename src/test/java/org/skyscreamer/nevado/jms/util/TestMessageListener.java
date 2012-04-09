package org.skyscreamer.nevado.jms.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test MessageListener.  Listens for messages, and adds them to an array for later review.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TestMessageListener implements MessageListener {
    private final Log _log = LogFactory.getLog(getClass());
    private final List<Message> _messages = new ArrayList<Message>();

    public void onMessage(Message message) {
        _messages.add(message);
        try {
            message.acknowledge();
        } catch (JMSException e) {
            _log.error("Unable to acknowledge message", e);
            throw new RuntimeException(e);
        }
    }

    public List<Message> getMessages() {
        return _messages;
    }
}
