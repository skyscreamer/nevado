package org.skyscreamer.nevado.jms.message;

import org.activemq.message.ActiveMQMapMessage;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.RandomData;

import javax.jms.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 6:44 PM
 */
public class MapMessageTest extends AbstractJMSTest {
    @Test
    public void testMapMessage() throws JMSException {
        MapMessage msg = createSession().createMapMessage();
        testMapMessage(msg);
    }

    @Test
    public void testAlienMapMessage() throws JMSException {
        MapMessage msg = new ActiveMQMapMessage();
        testMapMessage(msg);
    }

    private void testMapMessage(MapMessage msg) throws JMSException {
        clearTestQueue();

        // Initialize MapMessage
        TestValues testValues = new TestValues();
        msg.setBoolean("bb", testValues.bb);
        msg.setString("sb", testValues.sb);
        msg.setByte("yy", testValues.yy);
        msg.setString("sy", testValues.sy);
        msg.setByte("yh", testValues.yh);
        msg.setShort("hh", testValues.hh);
        msg.setString("sh", testValues.sh);
        msg.setChar("cc", testValues.cc);
        msg.setByte("yi", testValues.yi);
        msg.setShort("hi", testValues.hi);
        msg.setInt("ii", testValues.ii);
        msg.setString("si", testValues.si);
        msg.setByte("yl", testValues.yl);
        msg.setShort("hl", testValues.hl);
        msg.setInt("il", testValues.il);
        msg.setLong("ll", testValues.ll);
        msg.setString("sl", testValues.sl);
        msg.setFloat("ff", testValues.ff);
        msg.setString("sf", testValues.sf);
        msg.setFloat("fd", testValues.fd);
        msg.setDouble("dd", testValues.dd);
        msg.setString("sd", testValues.sd);
        msg.setBoolean("bs", testValues.bs);
        msg.setByte("ys", testValues.ys);
        msg.setShort("hs", testValues.hs);
        msg.setChar("cs", testValues.cs);
        msg.setInt("is", testValues.is);
        msg.setLong("ls", testValues.ls);
        msg.setFloat("fs", testValues.fs);
        msg.setDouble("ds", testValues.ds);
        msg.setString("ss", testValues.ss);
        msg.setBytes("zz", testValues.zz);

        // Send/Receive
        MapMessage msgOut = (MapMessage)sendAndReceive(msg);
        Assert.assertTrue("Should be a map message", msgOut instanceof MapMessage);

        // Verify
        Assert.assertEquals("MapMessage.getBoolean failed (conversion bb)", testValues.bb, msgOut.getBoolean("bb"));
        Assert.assertEquals("MapMessage.getBoolean failed (conversion sb)", testValues.sb, String.valueOf(msgOut.getBoolean("sb")));
        Assert.assertEquals("MapMessage.getByte failed (conversion yy)", testValues.yy, msgOut.getByte("yy"));
        Assert.assertEquals("MapMessage.getByte failed (conversion sy)", testValues.sy, String.valueOf(msgOut.getByte("sy")));
        Assert.assertEquals("MapMessage.getShort failed (conversion yh)", testValues.yh, msgOut.getShort("yh"));
        Assert.assertEquals("MapMessage.getShort failed (conversion hh)", testValues.hh, msgOut.getShort("hh"));
        Assert.assertEquals("MapMessage.getShort failed (conversion sh)", testValues.sh, String.valueOf(msgOut.getShort("sh")));
        Assert.assertEquals("MapMessage.getShort failed (conversion cc)", testValues.cc, msgOut.getChar("cc"));
        Assert.assertEquals("MapMessage.getInt failed (conversion yi)", testValues.yi, msgOut.getInt("yi"));
        Assert.assertEquals("MapMessage.getInt failed (conversion hi)", testValues.hi, msgOut.getInt("hi"));
        Assert.assertEquals("MapMessage.getInt failed (conversion ii)", testValues.ii, msgOut.getInt("ii"));
        Assert.assertEquals("MapMessage.getInt failed (conversion si)", testValues.si, String.valueOf(msgOut.getInt("si")));
        Assert.assertEquals("MapMessage.getLong failed (conversion yl)", testValues.yl, msgOut.getLong("yl"));
        Assert.assertEquals("MapMessage.getLong failed (conversion hl)", testValues.hl, msgOut.getLong("hl"));
        Assert.assertEquals("MapMessage.getLong failed (conversion il)", testValues.il, msgOut.getLong("il"));
        Assert.assertEquals("MapMessage.getLong failed (conversion ll)", testValues.ll, msgOut.getLong("ll"));
        Assert.assertEquals("MapMessage.getLong failed (conversion sl)", testValues.sl, String.valueOf(msgOut.getLong("sl")));
        Assert.assertEquals("MapMessage.getFloat failed (conversion ff)", testValues.ff, msgOut.getFloat("ff"), 0.0001);
        Assert.assertEquals("MapMessage.getFloat failed (conversion sf)", testValues.sf, String.valueOf(msgOut.getFloat("sf")));
        Assert.assertEquals("MapMessage.getDouble failed (conversion fd)", testValues.fd, msgOut.getDouble("fd"), 0.0001);
        Assert.assertEquals("MapMessage.getDouble failed (conversion dd)", testValues.dd, msgOut.getDouble("dd"), 0.0001);
        Assert.assertEquals("MapMessage.getDouble failed (conversion sd)", testValues.sd.substring(0, 8), String.valueOf(msgOut.getDouble("sd")).substring(0, 8));
        Assert.assertEquals("MapMessage.getString failed (conversion bs)", String.valueOf(testValues.bs), msgOut.getString("bs"));
        Assert.assertEquals("MapMessage.getString failed (conversion ys)", String.valueOf(testValues.ys), msgOut.getString("ys"));
        Assert.assertEquals("MapMessage.getString failed (conversion hs)", String.valueOf(testValues.hs), msgOut.getString("hs"));
        Assert.assertEquals("MapMessage.getString failed (conversion cs)", String.valueOf(testValues.cs), msgOut.getString("cs"));
        Assert.assertEquals("MapMessage.getString failed (conversion is)", String.valueOf(testValues.is), msgOut.getString("is"));
        Assert.assertEquals("MapMessage.getString failed (conversion ls)", String.valueOf(testValues.ls), msgOut.getString("ls"));
        Assert.assertEquals("MapMessage.getString failed (conversion fs)", String.valueOf(testValues.fs), msgOut.getString("fs"));
        Assert.assertEquals("MapMessage.getString failed (conversion ds)", String.valueOf(testValues.ds), msgOut.getString("ds"));
        Assert.assertEquals("MapMessage.getString failed (conversion ss)", String.valueOf(testValues.ss), msgOut.getString("ss"));
        Assert.assertTrue("MapMessage.getBytes failed (conversion zz)", Arrays.equals(testValues.zz, msgOut.getBytes("zz")));
    }

    @Test(expected = MessageFormatException.class)
    public void ybFail() throws JMSException { initMap(RandomData.readByte()).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void sbFail() throws JMSException { initMap(RandomData.readShort()).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void cbFail() throws JMSException { initMap(RandomData.readChar()).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void ibFail() throws JMSException { initMap(RandomData.readInt()).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void lbFail() throws JMSException { initMap(RandomData.readLong()).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void fbFail() throws JMSException { initMap(RandomData.readFloat()).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void dbFail() throws JMSException { initMap(RandomData.readDouble()).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void zbFail() throws JMSException { initMap(RandomData.readBytes(10)).getBoolean("X"); }

    @Test(expected = MessageFormatException.class)
    public void byFail() throws JMSException { initMap(RandomData.readBoolean()).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void syFail() throws JMSException { initMap(RandomData.readShort()).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void cyFail() throws JMSException { initMap(RandomData.readChar()).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void iyFail() throws JMSException { initMap(RandomData.readInt()).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void lyFail() throws JMSException { initMap(RandomData.readLong()).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void fyFail() throws JMSException { initMap(RandomData.readFloat()).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void dyFail() throws JMSException { initMap(RandomData.readDouble()).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void zyFail() throws JMSException { initMap(RandomData.readBytes(10)).getByte("X"); }

    @Test(expected = MessageFormatException.class)
    public void bhFail() throws JMSException { initMap(RandomData.readBoolean()).getShort("X"); }

    @Test(expected = MessageFormatException.class)
    public void chFail() throws JMSException { initMap(RandomData.readChar()).getShort("X"); }

    @Test(expected = MessageFormatException.class)
    public void ihFail() throws JMSException { initMap(RandomData.readInt()).getShort("X"); }

    @Test(expected = MessageFormatException.class)
    public void lhFail() throws JMSException { initMap(RandomData.readLong()).getShort("X"); }

    @Test(expected = MessageFormatException.class)
    public void fhFail() throws JMSException { initMap(RandomData.readFloat()).getShort("X"); }

    @Test(expected = MessageFormatException.class)
    public void dhFail() throws JMSException { initMap(RandomData.readDouble()).getShort("X"); }

    @Test(expected = MessageFormatException.class)
    public void zhFail() throws JMSException { initMap(RandomData.readBytes(10)).getShort("X"); }

    @Test(expected = MessageFormatException.class)
    public void bcFail() throws JMSException { initMap(RandomData.readBoolean()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void ycFail() throws JMSException { initMap(RandomData.readByte()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void hcFail() throws JMSException { initMap(RandomData.readShort()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void icFail() throws JMSException { initMap(RandomData.readInt()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void lcFail() throws JMSException { initMap(RandomData.readLong()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void fcFail() throws JMSException { initMap(RandomData.readFloat()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void dcFail() throws JMSException { initMap(RandomData.readDouble()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void scFail() throws JMSException { initMap(RandomData.readString()).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void zcFail() throws JMSException { initMap(RandomData.readBytes(10)).getChar("X"); }

    @Test(expected = MessageFormatException.class)
    public void biFail() throws JMSException { initMap(RandomData.readBoolean()).getInt("X"); }

    @Test(expected = MessageFormatException.class)
    public void ciFail() throws JMSException { initMap(RandomData.readChar()).getInt("X"); }

    @Test(expected = MessageFormatException.class)
    public void liFail() throws JMSException { initMap(RandomData.readLong()).getInt("X"); }

    @Test(expected = MessageFormatException.class)
    public void fiFail() throws JMSException { initMap(RandomData.readFloat()).getInt("X"); }

    @Test(expected = MessageFormatException.class)
    public void diFail() throws JMSException { initMap(RandomData.readDouble()).getInt("X"); }

    @Test(expected = MessageFormatException.class)
    public void ziFail() throws JMSException { initMap(RandomData.readBytes(10)).getInt("X"); }

    @Test(expected = MessageFormatException.class)
    public void blFail() throws JMSException { initMap(RandomData.readBoolean()).getLong("X"); }

    @Test(expected = MessageFormatException.class)
    public void clFail() throws JMSException { initMap(RandomData.readChar()).getLong("X"); }

    @Test(expected = MessageFormatException.class)
    public void dlFail() throws JMSException { initMap(RandomData.readDouble()).getLong("X"); }

    @Test(expected = MessageFormatException.class)
    public void flFail() throws JMSException { initMap(RandomData.readFloat()).getLong("X"); }

    @Test(expected = MessageFormatException.class)
    public void zlFail() throws JMSException { initMap(RandomData.readBytes(10)).getLong("X"); }

    @Test(expected = MessageFormatException.class)
    public void bfFail() throws JMSException { initMap(RandomData.readBoolean()).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void yfFail() throws JMSException { initMap(RandomData.readByte()).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void hfFail() throws JMSException { initMap(RandomData.readShort()).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void cfFail() throws JMSException { initMap(RandomData.readChar()).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void ifFail() throws JMSException { initMap(RandomData.readInt()).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void lfFail() throws JMSException { initMap(RandomData.readLong()).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void dfFail() throws JMSException { initMap(RandomData.readDouble()).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void zfFail() throws JMSException { initMap(RandomData.readBytes(10)).getFloat("X"); }

    @Test(expected = MessageFormatException.class)
    public void bdFail() throws JMSException { initMap(RandomData.readBoolean()).getDouble("X"); }

    @Test(expected = MessageFormatException.class)
    public void ydFail() throws JMSException { initMap(RandomData.readByte()).getDouble("X"); }

    @Test(expected = MessageFormatException.class)
    public void hdFail() throws JMSException { initMap(RandomData.readShort()).getDouble("X"); }

    @Test(expected = MessageFormatException.class)
    public void cdFail() throws JMSException { initMap(RandomData.readChar()).getDouble("X"); }

    @Test(expected = MessageFormatException.class)
    public void idFail() throws JMSException { initMap(RandomData.readInt()).getDouble("X"); }

    @Test(expected = MessageFormatException.class)
    public void ldFail() throws JMSException { initMap(RandomData.readLong()).getDouble("X"); }

    @Test(expected = MessageFormatException.class)
    public void zdFail() throws JMSException { initMap(RandomData.readBytes(10)).getDouble("X"); }

    @Test(expected = MessageFormatException.class)
    public void zsFail() throws JMSException { initMap(RandomData.readBytes(10)).getString("X"); }

    @Test(expected = MessageFormatException.class)
    public void bzFail() throws JMSException { initMap(RandomData.readBoolean()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void yzFail() throws JMSException { initMap(RandomData.readByte()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void hzFail() throws JMSException { initMap(RandomData.readShort()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void czFail() throws JMSException { initMap(RandomData.readChar()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void izFail() throws JMSException { initMap(RandomData.readInt()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void lzFail() throws JMSException { initMap(RandomData.readLong()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void fzFail() throws JMSException { initMap(RandomData.readFloat()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void dzFail() throws JMSException { initMap(RandomData.readDouble()).getBytes("X"); }

    @Test(expected = MessageFormatException.class)
    public void szFail() throws JMSException { initMap(RandomData.readString()).getBytes("X"); }


    private MapMessage initMap(Object o) throws JMSException {
        MapMessage mapMessage = createSession().createMapMessage();
        mapMessage.setObject("X", o);
        return mapMessage;
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
