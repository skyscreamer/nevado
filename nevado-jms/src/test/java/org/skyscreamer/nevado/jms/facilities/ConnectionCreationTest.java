package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.connector.typica.TypicaSQSConnector;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;

/**
 * Tests creation of a connection (JMS 1.1, Sec. 4.3.1)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionCreationTest extends AbstractJMSTest {
    @Test
    public void testBadConnection() throws JMSException {
        if (getConnection().getSQSConnector() instanceof TypicaSQSConnector)
        {
            boolean exceptionThrown = false;
            try {
                getConnectionFactory().createConnection("BADACCESSKEY", "BADSECRETKEY");
            } catch (JMSSecurityException e) {
                exceptionThrown = true;
            }
            Assert.assertTrue("Expected exception to be thrown", exceptionThrown);
        }
    }
}
