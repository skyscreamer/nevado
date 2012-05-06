package org.skyscreamer.nevado.jms.properties;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.message.JMSXProperty;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Test for section 3.5.9 of the JMS 1.1 Specification.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class JMSXPropertiesTest extends AbstractJMSTest {
    @Test
    public void testSupported() throws JMSException {
        ConnectionMetaData metaData = getConnection().getMetaData();
        Enumeration e = metaData.getJMSXPropertyNames();
        Set<String> supportedPropertyNames = new HashSet<String>();
        while(e.hasMoreElements()) {
            supportedPropertyNames.add((String)e.nextElement());
        }
        Assert.assertEquals(3, supportedPropertyNames.size());
        Assert.assertTrue(supportedPropertyNames.contains("JMSXGroupID"));
        Assert.assertTrue(supportedPropertyNames.contains("JMSXGroupSeq"));
        Assert.assertTrue(supportedPropertyNames.contains("JMSXDeliveryCount"));
    }

    @Test
    public void testSetJMSXProperty() throws JMSException {
        NevadoMessage msg = (NevadoMessage) createSession().createMessage();
        msg.setJMSXProperty(JMSXProperty.JMSXGroupID, "abc");
        NevadoMessage msgOut = (NevadoMessage)sendAndReceive(msg);
        Assert.assertEquals("abc", msgOut.getJMSXProperty(JMSXProperty.JMSXGroupID));
        Assert.assertEquals("abc", msgOut.getStringProperty("JMSXGroupID"));
    }
}
