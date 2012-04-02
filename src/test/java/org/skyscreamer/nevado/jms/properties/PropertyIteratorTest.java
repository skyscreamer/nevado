package org.skyscreamer.nevado.jms.properties;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.RandomData;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 4/1/12
 * Time: 9:03 PM
 */
public class PropertyIteratorTest extends AbstractJMSTest {
    @Test
    public void testIterator() throws JMSException {
        Message msg = getSession().createMessage();
        Properties properties = new Properties();
        for(int i = 0 ; i < 100 ; ++i) {
            String key = "a" + i;
            String value = RandomData.readString();
            properties.setProperty(key, value);
            msg.setStringProperty(key, value);
        }
        Message msgOut = sendAndReceive(msg);
        Enumeration e = msg.getPropertyNames();
        int count = 0;
        while(e.hasMoreElements()) {
            ++count;
            String key = (String)e.nextElement();
            String value = properties.getProperty(key);
            Assert.assertEquals(value, msg.getStringProperty(key));
        }
        Assert.assertEquals("Message did not return the expected # of properties", properties.size(), count);
    }
}
