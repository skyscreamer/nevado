package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.CloudCredentials;
import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSConnector;
import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSCredentials;

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
        CloudCredentials credentials = ((NevadoConnectionFactory)getConnectionFactory()).getCloudCredentials();
        if (credentials instanceof AmazonAwsSQSCredentials) {
            ((AmazonAwsSQSCredentials) credentials).setAwsAccessKey("BADACCESSKEY");
            boolean exceptionThrown = false;
            try {
                getConnectionFactory().createConnection();
            } catch (JMSSecurityException e) {
                exceptionThrown = true;
            }
            Assert.assertTrue("Expected exception to be thrown", exceptionThrown);
        }
    }
}
