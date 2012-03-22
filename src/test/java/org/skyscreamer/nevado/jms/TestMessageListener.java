package org.skyscreamer.nevado.jms;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/18/12
 * Time: 8:24 PM
 */
@Component("testMessageListener")
public class TestMessageListener implements MessageListener {
    private final Set<String> _messages = new HashSet<String>();

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                _messages.add(((TextMessage) message).getText());
            }
            catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            throw new IllegalArgumentException("Message must be of type TextMessage");
        }    
    }

    public Set<String> getMessages() {
        return _messages;
    }
}
