package org.skyscreamer.nevado.jms.message;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.RandomData;

import javax.jms.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/26/12
 * Time: 8:11 AM
 */
public class StreamMessageTest extends AbstractJMSTest {
    @Test
    public void testStreamMessage() throws JMSException {
        clearTestQueue();

        // Initialize MapMessage
        TestValues testValues = new TestValues();
        StreamMessage msg = getSession().createStreamMessage();
        msg.writeBoolean(testValues.bb);
        msg.writeString(testValues.sb);
        msg.writeByte(testValues.yy);
        msg.writeString(testValues.sy);
        msg.writeByte(testValues.yh);
        msg.writeShort(testValues.hh);
        msg.writeString(testValues.sh);
        msg.writeChar(testValues.cc);
        msg.writeByte(testValues.yi);
        msg.writeShort(testValues.hi);
        msg.writeInt(testValues.ii);
        msg.writeString(testValues.si);
        msg.writeByte(testValues.yl);
        msg.writeShort(testValues.hl);
        msg.writeInt(testValues.il);
        msg.writeLong(testValues.ll);
        msg.writeString(testValues.sl);
        msg.writeFloat(testValues.ff);
        msg.writeString(testValues.sf);
        msg.writeFloat(testValues.fd);
        msg.writeDouble(testValues.dd);
        msg.writeString(testValues.sd);
        msg.writeBoolean(testValues.bs);
        msg.writeByte(testValues.ys);
        msg.writeShort(testValues.hs);
        msg.writeChar(testValues.cs);
        msg.writeInt(testValues.is);
        msg.writeLong(testValues.ls);
        msg.writeFloat(testValues.fs);
        msg.writeDouble(testValues.ds);
        msg.writeString(testValues.ss);
        msg.writeBytes(testValues.zz);

        // Send/Receive
        getSession().createProducer(getTestQueue()).send(msg);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertTrue("Should be a stream message", msgOut instanceof StreamMessage);

        // Verify
        Assert.assertEquals("StreamMessage.getBoolean failed (conversion bb)", testValues.bb, msg.readBoolean());
        Assert.assertEquals("StreamMessage.getBoolean failed (conversion sb)", testValues.sb, String.valueOf(msg.readBoolean()));
        Assert.assertEquals("StreamMessage.getByte failed (conversion yy)", testValues.yy, msg.readByte());
        Assert.assertEquals("StreamMessage.getByte failed (conversion sy)", testValues.sy, String.valueOf(msg.readByte()));
        Assert.assertEquals("StreamMessage.getShort failed (conversion yh)", testValues.yh, msg.readShort());
        Assert.assertEquals("StreamMessage.getShort failed (conversion hh)", testValues.hh, msg.readShort());
        Assert.assertEquals("StreamMessage.getShort failed (conversion sh)", testValues.sh, String.valueOf(msg.readShort()));
        Assert.assertEquals("StreamMessage.getShort failed (conversion cc)", testValues.cc, msg.readChar());
        Assert.assertEquals("StreamMessage.getInt failed (conversion yi)", testValues.yi, msg.readInt());
        Assert.assertEquals("StreamMessage.getInt failed (conversion hi)", testValues.hi, msg.readInt());
        Assert.assertEquals("StreamMessage.getInt failed (conversion ii)", testValues.ii, msg.readInt());
        Assert.assertEquals("StreamMessage.getInt failed (conversion si)", testValues.si, String.valueOf(msg.readInt()));
        Assert.assertEquals("StreamMessage.getLong failed (conversion yl)", testValues.yl, msg.readLong());
        Assert.assertEquals("StreamMessage.getLong failed (conversion hl)", testValues.hl, msg.readLong());
        Assert.assertEquals("StreamMessage.getLong failed (conversion il)", testValues.il, msg.readLong());
        Assert.assertEquals("StreamMessage.getLong failed (conversion ll)", testValues.ll, msg.readLong());
        Assert.assertEquals("StreamMessage.getLong failed (conversion sl)", testValues.sl, String.valueOf(msg.readLong()));
        Assert.assertEquals("StreamMessage.getFloat failed (conversion ff)", testValues.ff, msg.readFloat(), 0.0001);
        Assert.assertEquals("StreamMessage.getFloat failed (conversion sf)", testValues.sf, String.valueOf(msg.readFloat()));
        Assert.assertEquals("StreamMessage.getDouble failed (conversion fd)", testValues.fd, msg.readDouble(), 0.0001);
        Assert.assertEquals("StreamMessage.getDouble failed (conversion dd)", testValues.dd, msg.readDouble(), 0.0001);
        Assert.assertEquals("StreamMessage.getDouble failed (conversion sd)", testValues.sd, String.valueOf(msg.readDouble()));
        Assert.assertEquals("StreamMessage.getString failed (conversion bs)", String.valueOf(testValues.bs), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion ys)", String.valueOf(testValues.ys), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion hs)", String.valueOf(testValues.hs), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion cs)", String.valueOf(testValues.cs), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion is)", String.valueOf(testValues.is), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion ls)", String.valueOf(testValues.ls), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion fs)", String.valueOf(testValues.fs), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion ds)", String.valueOf(testValues.ds), msg.readString());
        Assert.assertEquals("StreamMessage.getString failed (conversion ss)", String.valueOf(testValues.ss), msg.readString());

        // Testing byte[] takes a little work
        byte[] buffer = new byte[10000];
        int count = msg.readBytes(buffer);
        Assert.assertTrue("Buffer too small", count < buffer.length);
        byte[] value = new byte[count];
        System.arraycopy(buffer, 0, value,  0, count);
        Assert.assertTrue("StreamMessage.getBytes failed (conversion zz)", Arrays.equals(testValues.zz, value));
    }

    @Test(expected = MessageFormatException.class)
    public void ybFail() throws JMSException { initStream(RandomData.readByte()).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void sbFail() throws JMSException { initStream(RandomData.readShort()).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void cbFail() throws JMSException { initStream(RandomData.readChar()).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void ibFail() throws JMSException { initStream(RandomData.readInt()).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void lbFail() throws JMSException { initStream(RandomData.readLong()).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void fbFail() throws JMSException {
        initStream(RandomData.readFloat()).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void dbFail() throws JMSException { initStream(RandomData.readDouble()).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void zbFail() throws JMSException { initStream(RandomData.readBytes(10)).readBoolean(); }

    @Test(expected = MessageFormatException.class)
    public void byFail() throws JMSException { initStream(RandomData.readBoolean()).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void syFail() throws JMSException { initStream(RandomData.readShort()).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void cyFail() throws JMSException { initStream(RandomData.readChar()).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void iyFail() throws JMSException { initStream(RandomData.readInt()).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void lyFail() throws JMSException { initStream(RandomData.readLong()).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void fyFail() throws JMSException { initStream(RandomData.readFloat()).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void dyFail() throws JMSException { initStream(RandomData.readDouble()).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void zyFail() throws JMSException { initStream(RandomData.readBytes(10)).readByte(); }

    @Test(expected = MessageFormatException.class)
    public void bhFail() throws JMSException { initStream(RandomData.readBoolean()).readShort(); }

    @Test(expected = MessageFormatException.class)
    public void chFail() throws JMSException { initStream(RandomData.readChar()).readShort(); }

    @Test(expected = MessageFormatException.class)
    public void ihFail() throws JMSException { initStream(RandomData.readInt()).readShort();
    }

    @Test(expected = MessageFormatException.class)
    public void lhFail() throws JMSException { initStream(RandomData.readLong()).readShort(); }

    @Test(expected = MessageFormatException.class)
    public void fhFail() throws JMSException { initStream(RandomData.readFloat()).readShort(); }

    @Test(expected = MessageFormatException.class)
    public void dhFail() throws JMSException { initStream(RandomData.readDouble()).readShort(); }

    @Test(expected = MessageFormatException.class)
    public void zhFail() throws JMSException { initStream(RandomData.readBytes(10)).readShort(); }

    @Test(expected = MessageFormatException.class)
    public void bcFail() throws JMSException { initStream(RandomData.readBoolean()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void ycFail() throws JMSException { initStream(RandomData.readByte()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void hcFail() throws JMSException {
        initStream(RandomData.readShort()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void icFail() throws JMSException { initStream(RandomData.readInt()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void lcFail() throws JMSException { initStream(RandomData.readLong()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void fcFail() throws JMSException { initStream(RandomData.readFloat()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void dcFail() throws JMSException { initStream(RandomData.readDouble()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void scFail() throws JMSException { initStream(RandomData.readString()).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void zcFail() throws JMSException { initStream(RandomData.readBytes(10)).readChar(); }

    @Test(expected = MessageFormatException.class)
    public void biFail() throws JMSException { initStream(RandomData.readBoolean()).readInt(); }

    @Test(expected = MessageFormatException.class)
    public void ciFail() throws JMSException { initStream(RandomData.readChar()).readInt(); }

    @Test(expected = MessageFormatException.class)
    public void liFail() throws JMSException { initStream(RandomData.readLong()).readInt(); }

    @Test(expected = MessageFormatException.class)
    public void fiFail() throws JMSException { initStream(RandomData.readFloat()).readInt(); }

    @Test(expected = MessageFormatException.class)
    public void diFail() throws JMSException { initStream(RandomData.readDouble()).readInt(); }

    @Test(expected = MessageFormatException.class)
    public void ziFail() throws JMSException { initStream(RandomData.readBytes(10)).readInt(); }

    @Test(expected = MessageFormatException.class)
    public void blFail() throws JMSException { initStream(RandomData.readBoolean()).readLong();
    }

    @Test(expected = MessageFormatException.class)
    public void clFail() throws JMSException { initStream(RandomData.readChar()).readLong(); }

    @Test(expected = MessageFormatException.class)
    public void dlFail() throws JMSException { initStream(RandomData.readDouble()).readLong(); }

    @Test(expected = MessageFormatException.class)
    public void flFail() throws JMSException { initStream(RandomData.readFloat()).readLong(); }

    @Test(expected = MessageFormatException.class)
    public void zlFail() throws JMSException { initStream(RandomData.readBytes(10)).readLong(); }

    @Test(expected = MessageFormatException.class)
    public void bfFail() throws JMSException { initStream(RandomData.readBoolean()).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void yfFail() throws JMSException { initStream(RandomData.readByte()).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void hfFail() throws JMSException {
        initStream(RandomData.readShort()).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void cfFail() throws JMSException { initStream(RandomData.readChar()).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void ifFail() throws JMSException { initStream(RandomData.readInt()).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void lfFail() throws JMSException { initStream(RandomData.readLong()).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void dfFail() throws JMSException { initStream(RandomData.readDouble()).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void zfFail() throws JMSException { initStream(RandomData.readBytes(10)).readFloat(); }

    @Test(expected = MessageFormatException.class)
    public void bdFail() throws JMSException { initStream(RandomData.readBoolean()).readDouble(); }

    @Test(expected = MessageFormatException.class)
    public void ydFail() throws JMSException { initStream(RandomData.readByte()).readDouble(); }

    @Test(expected = MessageFormatException.class)
    public void hdFail() throws JMSException { initStream(RandomData.readShort()).readDouble(); }

    @Test(expected = MessageFormatException.class)
    public void cdFail() throws JMSException { initStream(RandomData.readChar()).readDouble(); }

    @Test(expected = MessageFormatException.class)
    public void idFail() throws JMSException { initStream(RandomData.readInt()).readDouble(); }

    @Test(expected = MessageFormatException.class)
    public void ldFail() throws JMSException { initStream(RandomData.readLong()).readDouble(); }

    @Test(expected = MessageFormatException.class)
    public void zdFail() throws JMSException { initStream(RandomData.readBytes(10)).readDouble(); }

    @Test(expected = MessageFormatException.class)
    public void zsFail() throws JMSException { initStream(RandomData.readBytes(10)).readString(); }

    @Test(expected = MessageFormatException.class)
    public void bzFail() throws JMSException { initStream(RandomData.readBoolean()).readBytes(new byte[10]); }

    @Test(expected = MessageFormatException.class)
    public void yzFail() throws JMSException { initStream(RandomData.readByte()).readBytes(new byte[10]); }

    @Test(expected = MessageFormatException.class)
    public void hzFail() throws JMSException { initStream(RandomData.readShort()).readBytes(new byte[10]); }

    @Test(expected = MessageFormatException.class)
    public void czFail() throws JMSException { initStream(RandomData.readChar()).readBytes(new byte[10]); }

    @Test(expected = MessageFormatException.class)
    public void izFail() throws JMSException { initStream(RandomData.readInt()).readBytes(new byte[10]); }

    // @Test(expected = MessageFormatException.class) - TODO - Weird fail!
    public void lzFail() throws JMSException { initStream(RandomData.readLong()).readBytes(new byte[10]); }

    @Test(expected = MessageFormatException.class)
    public void fzFail() throws JMSException { initStream(RandomData.readFloat()).readBytes(new byte[10]); }

    @Test(expected = MessageFormatException.class)
    public void dzFail() throws JMSException { initStream(RandomData.readDouble()).readBytes(new byte[10]); }

    @Test(expected = MessageFormatException.class)
    public void szFail() throws JMSException { initStream(RandomData.readString()).readBytes(new byte[10]); }

    private StreamMessage initStream(Object o) throws JMSException {
        StreamMessage streamMessage = getSession().createStreamMessage();
        streamMessage.writeObject(o);
        streamMessage.writeBytes(new byte[10]);
        ((NevadoStreamMessage)streamMessage).onSend();
        return streamMessage;
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
