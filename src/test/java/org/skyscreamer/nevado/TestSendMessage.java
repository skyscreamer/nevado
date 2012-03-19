package org.skyscreamer.nevado;

import javax.jms.*;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/18/12
 * Time: 7:51 PM
 */
public class TestSendMessage {
    private JmsTemplate _jmsTemplate; // Configured via setter
    @Autowired private Queue _queue;
    @Autowired @Qualifier("testMessageListener") private TestMessageListener testMessageListener;

    @Test
    public void testRoundTrip() throws Exception {
        final String key = "testkey_" + new Random().nextInt();
        _jmsTemplate.send(_queue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(key);
            }
        });
        Thread.sleep(1000); // Need this long?
        Assert.assertTrue("Looking for key " + key, testMessageListener.getMessages().contains(key));
    }

    // Spring injection points
    @Autowired
    public void setConnectionFactory(ConnectionFactory cf) {
        _jmsTemplate = new JmsTemplate(cf);
    }
}
