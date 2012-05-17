package org.skyscreamer.nevado.jms.connector.typica;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.connector.typica.TypicaSQSConnector;

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
        ((TypicaSQSConnector)getConnection().getSQSConnector())._queueService.setServer("an.invalid.server");
        getConnection().getSQSConnector().test();
    }
}
