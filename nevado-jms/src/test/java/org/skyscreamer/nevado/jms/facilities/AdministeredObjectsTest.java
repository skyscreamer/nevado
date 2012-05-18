package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;

import javax.naming.Referenceable;
import java.io.Serializable;

/**
 * Tests header rules of JMS 1.1 Sec 4.2
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class AdministeredObjectsTest {
    @Test
    public void testsImplements() {
        Assert.assertTrue(Serializable.class.isAssignableFrom(NevadoDestination.class));
        Assert.assertTrue(Referenceable.class.isAssignableFrom(NevadoDestination.class));
        Assert.assertTrue(Serializable.class.isAssignableFrom(NevadoConnectionFactory.class));
        Assert.assertTrue(Referenceable.class.isAssignableFrom(NevadoConnectionFactory.class));
    }
}
