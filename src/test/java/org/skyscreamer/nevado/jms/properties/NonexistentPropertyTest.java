package org.skyscreamer.nevado.jms.properties;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 4/1/12
 * Time: 4:47 PM
 */
public class NonexistentPropertyTest extends AbstractJMSTest {
    @Test
    public void testNonexistentProperty() throws JMSException {
        Message msg = getSession().createMessage();
        Assert.assertNull(msg.getStringProperty("noSuchProperty"));
        Assert.assertNull(msg.getObjectProperty("noSuchProperty"));
    }
}
