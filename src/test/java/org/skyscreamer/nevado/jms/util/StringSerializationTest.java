package org.skyscreamer.nevado.jms.util;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.RandomData;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 4/5/12
 * Time: 8:52 AM
 */
public class StringSerializationTest {
    @Test
    public void testChar() throws IOException {
        Character c = RandomData.readChar();
        String serialized = SerializeStringUtil.serialize(c);
        Object[] objects = SerializeStringUtil.deserialize(serialized);
        Assert.assertEquals(1, objects.length);
        Assert.assertEquals(Character.class, objects[0].getClass());
        Assert.assertEquals(c, objects[0]);
    }
}
