package org.skyscreamer.nevado.jms.message;

import org.activemq.message.ActiveMQBytesMessage;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/26/12
 * Time: 6:53 PM
 */
public class BytesMessageTest extends AbstractJMSTest {
    @Test
    public void testBytesMessage() throws JMSException {
        BytesMessage msg = createSession().createBytesMessage();
        testBytesMessage(msg);
    }

    @Test
    public void testAlienBytesMessage() throws JMSException {
        BytesMessage msg = new ActiveMQBytesMessage();
        testBytesMessage(msg);
    }

    private void testBytesMessage(BytesMessage msg) throws JMSException {
        clearTestQueue();

        // Initialize MapMessage
        TestValues testValues = new TestValues();
        msg.writeBoolean(testValues.bb);
        msg.writeByte(testValues.yy);
        msg.writeShort(testValues.hh);
        msg.writeChar(testValues.cc);
        msg.writeInt(testValues.ii);
        msg.writeLong(testValues.ll);
        msg.writeFloat(testValues.ff);
        msg.writeDouble(testValues.dd);
        msg.writeUTF(testValues.ss);
        msg.writeBytes(testValues.zz);

        // Send/Receive
        BytesMessage msgOut = (BytesMessage)sendAndReceive(msg);
        Assert.assertTrue("Should be a stream message", msgOut instanceof BytesMessage);

        // Verify
        Assert.assertEquals("BytesMessage.getBoolean failed (conversion bb)", testValues.bb, msgOut.readBoolean());
        Assert.assertEquals("BytesMessage.getByte failed (conversion yy)", testValues.yy, msgOut.readByte());
        Assert.assertEquals("BytesMessage.getShort failed (conversion hh)", testValues.hh, msgOut.readShort());
        Assert.assertEquals("BytesMessage.getShort failed (conversion cc)", testValues.cc, msgOut.readChar());
        Assert.assertEquals("BytesMessage.getInt failed (conversion ii)", testValues.ii, msgOut.readInt());
        Assert.assertEquals("BytesMessage.getLong failed (conversion ll)", testValues.ll, msgOut.readLong());
        Assert.assertEquals("BytesMessage.getFloat failed (conversion ff)", testValues.ff, msgOut.readFloat(), 0.0001);
        Assert.assertEquals("BytesMessage.getDouble failed (conversion dd)", testValues.dd, msgOut.readDouble(), 0.0001);
        Assert.assertEquals("BytesMessage.getString failed (conversion ss)", String.valueOf(testValues.ss), msgOut.readUTF());

        // Testing byte[] takes a little work
        byte[] buffer = new byte[10000];
        int count = msgOut.readBytes(buffer);
        Assert.assertTrue("Buffer too small", count < buffer.length);
        byte[] value = new byte[count];
        System.arraycopy(buffer, 0, value,  0, count);
        Assert.assertTrue("BytesMessage.getBytes failed (conversion zz)", Arrays.equals(testValues.zz, value));
    }

    private class TestValues {
        final boolean bb = RandomData.readBoolean();
        final String sb = RandomData.readBoolean().toString();
        final byte yy = RandomData.readByte();
        final String sy = RandomData.readByte().toString();
        final byte yh = RandomData.readByte();
        final short hh = RandomData.readShort();
        final String sh = RandomData.readShort().toString();
        final char cc = RandomData.readChar();
        final byte yi = RandomData.readByte();
        final short hi = RandomData.readShort();
        final int ii = RandomData.readInt();
        final String si = RandomData.readInt().toString();
        final byte yl = RandomData.readByte();
        final short hl = RandomData.readShort();
        final int il = RandomData.readInt();
        final long ll = RandomData.readLong();
        final String sl = RandomData.readLong().toString();
        final float ff = RandomData.readFloat();
        final String sf = RandomData.readFloat().toString();
        final float fd = RandomData.readFloat();
        final double dd = RandomData.readDouble();
        final String sd = RandomData.readDouble().toString();
        final boolean bs = RandomData.readBoolean();
        final byte ys = RandomData.readByte();
        final short hs = RandomData.readShort();
        final char cs = RandomData.readChar();
        final int is = RandomData.readInt();
        final long ls = RandomData.readLong();
        final float fs = RandomData.readFloat();
        final double ds = RandomData.readDouble();
        final String ss = RandomData.readString();
        final byte[] zz = RandomData.readBytes(1000);
    }
}
