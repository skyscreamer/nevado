package org.skyscreamer.nevado.jms.facilities;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;

/**
 * Tests creation of a connection (JMS 1.1, Sec. 4.3.1)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionCreationTest extends AbstractJMSTest {
    @Test(expected = JMSSecurityException.class)
    public void testBadConnection() throws JMSException {
        getConnectionFactory().createConnection("BADACCESSKEY", "BADSECRETKEY");
    }
}
