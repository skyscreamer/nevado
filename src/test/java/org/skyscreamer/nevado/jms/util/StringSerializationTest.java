package org.skyscreamer.nevado.jms.util;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.RandomData;

import java.io.IOException;

/**
 * Test work-around for serialization.  Hessian turns java.lang.Character into a String.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class StringSerializationTest {
    @Test
    public void testChar() throws IOException {
        Character c = RandomData.readChar();
        String serialized = SerializeUtil.serializeToString(c);
        Object object = SerializeUtil.deserializeFromString(serialized);
        Assert.assertEquals(Character.class, object.getClass());
        Assert.assertEquals(c, object);
    }
}
