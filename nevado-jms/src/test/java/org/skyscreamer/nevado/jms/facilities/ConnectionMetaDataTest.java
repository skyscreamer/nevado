package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;

/**
 * TODO - Description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ConnectionMetaDataTest extends AbstractJMSTest {
    @Test
    public void testMetaData() throws JMSException {
        Package p = getClass().getPackage();
        String version = p.getImplementationVersion();
        ConnectionMetaData metaData = getConnection().getMetaData();
        Assert.assertEquals("1.1", metaData.getJMSVersion());
    }
}
