package org.skyscreamer.nevado.jms.properties;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Test for section 3.5.8 of the JMS 1.1 Specification.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NonexistentPropertyTest extends AbstractJMSTest {
    @Test
    public void testNonexistentProperty() throws JMSException {
        Message msg = createSession().createMessage();
        Assert.assertNull(msg.getStringProperty("noSuchProperty"));
        Assert.assertNull(msg.getObjectProperty("noSuchProperty"));
    }
}
