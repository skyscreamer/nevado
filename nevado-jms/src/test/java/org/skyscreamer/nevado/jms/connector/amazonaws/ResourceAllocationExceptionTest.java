package org.skyscreamer.nevado.jms.connector.amazonaws;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSConnector;
import org.skyscreamer.nevado.jms.connector.mock.MockSQSConnector;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

/**
 * Test ResourceAllocationException
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ResourceAllocationExceptionTest extends AbstractJMSTest {
    @Test(expected = ResourceAllocationException.class)
    public void testSQSResourceAllocationException() throws JMSException {
        if (getConnection().getSQSConnector() instanceof MockSQSConnector) {
            throw new ResourceAllocationException("mock won't break on its own");
        } else {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            connectionFactory.setAwsSQSEndpoint("a.deliberately.invalid.server");
            createConnection(connectionFactory);
        }
    }

    @Test(expected = ResourceAllocationException.class)
    public void testSNSResourceAllocationException() throws JMSException {
        if (getConnection().getSQSConnector() instanceof MockSQSConnector) {
            throw new ResourceAllocationException("mock won't break on its own");
        } else {
            NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
            connectionFactory.setAwsSNSEndpoint("a.deliberately.invalid.server");
            createConnection(connectionFactory);
        }
    }
}
