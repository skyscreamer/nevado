package org.skyscreamer.nevado.jms.connector;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.ResourceAllocationException;

/**
 * Test ResourceAllocationException
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ResourceAllocationExceptionTest extends AbstractJMSTest {
    @Test(expected = ResourceAllocationException.class)
    public void testResourceAllocationException() throws JMSException {
        ((SQSConnector)getConnection().getSQSConnector())._queueService.setServer("an.invalid.server");
        getConnection().getSQSConnector().test();
    }
}
