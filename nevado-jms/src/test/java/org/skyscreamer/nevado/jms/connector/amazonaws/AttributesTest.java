package org.skyscreamer.nevado.jms.connector.amazonaws;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ResourceAllocationException;
import javax.jms.TextMessage;

/**
 * Created with IntelliJ IDEA.
 * User: carterp
 * Date: 1/11/14
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class AttributesTest extends AbstractJMSTest {
    @Test
    public void testSQSResourceAllocationException() throws JMSException {
        if (getConnection().getSQSConnector() instanceof AmazonAwsSQSConnector) {
            TextMessage msg = createSession().createTextMessage();
            String text = "How much wood could a woodchuck chuck?  " + RandomData.readInt() + " logs!";
            msg.setText(text);
            Message msgOut = sendAndReceive(msg);

            // Makes sure this is the message we sent
            Assert.assertTrue("Should be a text message", msgOut instanceof TextMessage);
            Assert.assertEquals("Text should match", text, ((TextMessage)msgOut).getText());

            // Look for Amazon AWS properties
            Assert.assertTrue(StringUtils.isNotEmpty(msgOut.getStringProperty("ApproximateFirstReceiveTimestamp")));
            Assert.assertTrue(StringUtils.isNotEmpty(msgOut.getStringProperty("ApproximateReceiveCount")));
            Assert.assertTrue(StringUtils.isNotEmpty(msgOut.getStringProperty("SenderId")));
            Assert.assertTrue(StringUtils.isNotEmpty(msgOut.getStringProperty("SentTimestamp")));
            Assert.assertFalse(StringUtils.isNotEmpty(msgOut.getStringProperty("BOGUS_PROPERTY")));
        }
    }
}
