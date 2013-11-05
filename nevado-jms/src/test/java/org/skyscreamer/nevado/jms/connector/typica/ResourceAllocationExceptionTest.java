package org.skyscreamer.nevado.jms.connector.typica;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSConnector;

import javax.jms.JMSException;
import javax.jms.ResourceAllocationException;

/**
 * Test ResourceAllocationException
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ResourceAllocationExceptionTest extends AbstractJMSTest {
    @Test
    public void testResourceAllocationException() throws JMSException {
        if (getConnection().getSQSConnector() instanceof AmazonAwsSQSConnector) {
            ((AmazonAwsSQSConnector)getConnection().getSQSConnector()).getAmazonSQS().setEndpoint("a.deliberately.invalid.server");
            boolean exceptionThrown = false;
            try {
                getConnection().getSQSConnector().test();
            } catch (ResourceAllocationException e) {
                exceptionThrown = true;
            }
            Assert.assertTrue("Expected exception to be thrown", exceptionThrown);
        }
    }
}
