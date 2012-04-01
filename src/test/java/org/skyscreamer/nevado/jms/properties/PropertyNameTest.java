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
 * Time: 3:23 PM
 */
public class PropertyNameTest extends AbstractJMSTest {
    private static final String VALID_FIRST_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_$";
    private static final String INVALID_FIRST_CHARS = " 0123456789~`!@#%^&*()-+=\\][|}{\":';?></.,";
    private static final String[] RESERVED_NAMES = {"NULL", "TRUE", "FALSE", "NOT", "AND", "OR", "BETWEEN",
        "LIKE", "IN", "IS", "ESCAPE"};
    private static final String WHITESPACE_CHARS = " \t\r\n";

    @Test
    public void testFirstCharacter() throws JMSException {
        Message msg = getSession().createMessage();
        String propertyName = "somename";
        for(char c : VALID_FIRST_CHARS.toCharArray()) {
            msg.setBooleanProperty(c + propertyName, true);
        }
        msg.clearProperties();

        for(char c : INVALID_FIRST_CHARS.toCharArray()) {
            try {
                msg.setBooleanProperty(c + propertyName, true);
            }
            catch (IllegalArgumentException e) {
                // Expected outcome
                continue;
            }
            Assert.fail("Did not throw expected exception for first char '" + c + "'");
        }
    }

    @Test
    public void testReservedNames() throws JMSException {
        Message msg = getSession().createMessage();
        for(String s : RESERVED_NAMES) {
            try {
                msg.setBooleanProperty(s, true);
            }
            catch (IllegalArgumentException e) {
                // Expected outcome
                continue;
            }
            Assert.fail("Did not throw expected exception for reserved name '" + s + "'");
        }
    }

    @Test
    public void testWhiteSpace() throws JMSException {
        Message msg = getSession().createMessage();
        int count = 0;
        for(char c : WHITESPACE_CHARS.toCharArray()) {
            ++count;
            try {
                msg.setBooleanProperty("some" + c + "name", true);
            }
            catch (IllegalArgumentException e) {
                // Expected outcome
                continue;
            }
            Assert.fail("Did not throw expected exception for whitespace (character " + count + ")");
        }
    }
}
