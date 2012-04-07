package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnection;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.RandomData;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Tests creation of a connection (JMS 1.1, Sec. 4.3.1)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionCreationTest extends AbstractJMSTest {
    @Test(expected = JMSSecurityException.class)
    public void testBadConnection() throws JMSException {
        new NevadoConnection("BADACCESSKEY", "BADSECRETKEY");
    }
}
