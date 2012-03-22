package org.skyscreamer.nevado.jms.message;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/22/12
 * Time: 3:22 AM
 */
public class TextMessageTest extends AbstractJMSTest {
    @Test
    public void testTextMessage1() throws JMSException {
        String text = "How much wood could a woodchuck chuck?  " + (new Random()).nextInt() + " logs!";
        TextMessage msg = getSession().createTextMessage();
        msg.setText(text);
        getSession().createProducer(getTestQueue()).send(msg);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertTrue("Should be a text message", msgOut instanceof TextMessage);
        Assert.assertEquals("Text should match", text, ((TextMessage)msgOut).getText());
    }

    @Test
    public void testTextMessage2() throws JMSException {
        String text = "How much wood could a woodchuck chuck?  " + (new Random()).nextInt() + " logs!";
        TextMessage msg = getSession().createTextMessage(text);
        getSession().createProducer(getTestQueue()).send(msg);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertTrue("Should be a text message", msgOut instanceof TextMessage);
        Assert.assertEquals("Text should match", text, ((TextMessage)msgOut).getText());
    }
}
