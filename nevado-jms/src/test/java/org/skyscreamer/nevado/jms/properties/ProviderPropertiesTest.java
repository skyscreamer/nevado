package org.skyscreamer.nevado.jms.properties;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Tests that client interactions with provider properties.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ProviderPropertiesTest extends AbstractJMSTest {
    @Test(expected = IllegalArgumentException.class)
    public void testDisallowClientSet() throws JMSException
    {
        Session session = createSession();
        Message msg = session.createMessage();
        msg.setStringProperty("JMS_nevadoBlah", "someproperty");
    }
}
