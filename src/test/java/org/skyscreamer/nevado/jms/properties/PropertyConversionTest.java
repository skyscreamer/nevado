package org.skyscreamer.nevado.jms.properties;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.RandomData;

import javax.jms.*;

/**
 * Test for section 3.5.4 of the JMS 1.1 Specification.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PropertyConversionTest extends AbstractJMSTest {
    @Test
    public void testSupportedConversions() throws JMSException {
        clearTestQueue();

        // Initialize Message properties
        TestValues testValues = new TestValues();
        Message msg = createSession().createMessage();
        msg.setBooleanProperty("bb", testValues.bb);
        msg.setStringProperty("sb", testValues.sb);
        msg.setByteProperty("yy", testValues.yy);
        msg.setStringProperty("sy", testValues.sy);
        msg.setByteProperty("yh", testValues.yh);
        msg.setShortProperty("hh", testValues.hh);
        msg.setStringProperty("sh", testValues.sh);
        msg.setByteProperty("yi", testValues.yi);
        msg.setShortProperty("hi", testValues.hi);
        msg.setIntProperty("ii", testValues.ii);
        msg.setStringProperty("si", testValues.si);
        msg.setByteProperty("yl", testValues.yl);
        msg.setShortProperty("hl", testValues.hl);
        msg.setIntProperty("il", testValues.il);
        msg.setLongProperty("ll", testValues.ll);
        msg.setStringProperty("sl", testValues.sl);
        msg.setFloatProperty("ff", testValues.ff);
        msg.setStringProperty("sf", testValues.sf);
        msg.setFloatProperty("fd", testValues.fd);
        msg.setDoubleProperty("dd", testValues.dd);
        msg.setStringProperty("sd", testValues.sd);
        msg.setBooleanProperty("bs", testValues.bs);
        msg.setByteProperty("ys", testValues.ys);
        msg.setShortProperty("hs", testValues.hs);
        msg.setIntProperty("is", testValues.is);
        msg.setLongProperty("ls", testValues.ls);
        msg.setFloatProperty("fs", testValues.fs);
        msg.setDoubleProperty("ds", testValues.ds);
        msg.setStringProperty("ss", testValues.ss);

        // Send/Receive
        Message msgOut = sendAndReceive(msg);

        // Verify
        Assert.assertEquals("Message.getBooleanProperty failed (conversion bb)", testValues.bb, msg.getBooleanProperty("bb"));
        Assert.assertEquals("Message.getBooleanProperty failed (conversion sb)", testValues.sb, String.valueOf(msg.getBooleanProperty("sb")));
        Assert.assertEquals("Message.getByteProperty failed (conversion yy)", testValues.yy, msg.getByteProperty("yy"));
        Assert.assertEquals("Message.getByteProperty failed (conversion sy)", testValues.sy, String.valueOf(msg.getByteProperty("sy")));
        Assert.assertEquals("Message.getShortProperty failed (conversion yh)", testValues.yh, msg.getShortProperty("yh"));
        Assert.assertEquals("Message.getShortProperty failed (conversion hh)", testValues.hh, msg.getShortProperty("hh"));
        Assert.assertEquals("Message.getShortProperty failed (conversion sh)", testValues.sh, String.valueOf(msg.getShortProperty("sh")));
        Assert.assertEquals("Message.getIntProperty failed (conversion yi)", testValues.yi, msg.getIntProperty("yi"));
        Assert.assertEquals("Message.getIntProperty failed (conversion hi)", testValues.hi, msg.getIntProperty("hi"));
        Assert.assertEquals("Message.getIntProperty failed (conversion ii)", testValues.ii, msg.getIntProperty("ii"));
        Assert.assertEquals("Message.getIntProperty failed (conversion si)", testValues.si, String.valueOf(msg.getIntProperty("si")));
        Assert.assertEquals("Message.getLongProperty failed (conversion yl)", testValues.yl, msg.getLongProperty("yl"));
        Assert.assertEquals("Message.getLongProperty failed (conversion hl)", testValues.hl, msg.getLongProperty("hl"));
        Assert.assertEquals("Message.getLongProperty failed (conversion il)", testValues.il, msg.getLongProperty("il"));
        Assert.assertEquals("Message.getLongProperty failed (conversion ll)", testValues.ll, msg.getLongProperty("ll"));
        Assert.assertEquals("Message.getLongProperty failed (conversion sl)", testValues.sl, String.valueOf(msg.getLongProperty("sl")));
        Assert.assertEquals("Message.getFloatProperty failed (conversion ff)", testValues.ff, msg.getFloatProperty("ff"), 0.0001);
        Assert.assertEquals("Message.getFloatProperty failed (conversion sf)", testValues.sf, String.valueOf(msg.getFloatProperty("sf")));
        Assert.assertEquals("Message.getDoubleProperty failed (conversion fd)", testValues.fd, msg.getDoubleProperty("fd"), 0.0001);
        Assert.assertEquals("Message.getDoubleProperty failed (conversion dd)", testValues.dd, msg.getDoubleProperty("dd"), 0.0001);
        Assert.assertEquals("Message.getDoubleProperty failed (conversion sd)", testValues.sd, String.valueOf(msg.getDoubleProperty("sd")));
        Assert.assertEquals("Message.getStringProperty failed (conversion bs)", String.valueOf(testValues.bs), msg.getStringProperty("bs"));
        Assert.assertEquals("Message.getStringProperty failed (conversion ys)", String.valueOf(testValues.ys), msg.getStringProperty("ys"));
        Assert.assertEquals("Message.getStringProperty failed (conversion hs)", String.valueOf(testValues.hs), msg.getStringProperty("hs"));
        Assert.assertEquals("Message.getStringProperty failed (conversion is)", String.valueOf(testValues.is), msg.getStringProperty("is"));
        Assert.assertEquals("Message.getStringProperty failed (conversion ls)", String.valueOf(testValues.ls), msg.getStringProperty("ls"));
        Assert.assertEquals("Message.getStringProperty failed (conversion fs)", String.valueOf(testValues.fs), msg.getStringProperty("fs"));
        Assert.assertEquals("Message.getStringProperty failed (conversion ds)", String.valueOf(testValues.ds), msg.getStringProperty("ds"));
        Assert.assertEquals("Message.getStringProperty failed (conversion ss)", String.valueOf(testValues.ss), msg.getStringProperty("ss"));
    }

    @Test(expected = MessageFormatException.class)
    public void ybFail() throws JMSException { initMap(RandomData.readByte()).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void sbFail() throws JMSException { initMap(RandomData.readShort()).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void cbFail() throws JMSException { initMap(RandomData.readChar()).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void ibFail() throws JMSException { initMap(RandomData.readInt()).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void lbFail() throws JMSException { initMap(RandomData.readLong()).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void fbFail() throws JMSException { initMap(RandomData.readFloat()).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void dbFail() throws JMSException { initMap(RandomData.readDouble()).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void zbFail() throws JMSException { initMap(RandomData.readBytes(10)).getBooleanProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void byFail() throws JMSException { initMap(RandomData.readBoolean()).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void syFail() throws JMSException { initMap(RandomData.readShort()).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void cyFail() throws JMSException { initMap(RandomData.readChar()).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void iyFail() throws JMSException { initMap(RandomData.readInt()).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void lyFail() throws JMSException { initMap(RandomData.readLong()).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void fyFail() throws JMSException { initMap(RandomData.readFloat()).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void dyFail() throws JMSException { initMap(RandomData.readDouble()).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void zyFail() throws JMSException { initMap(RandomData.readBytes(10)).getByteProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void bhFail() throws JMSException { initMap(RandomData.readBoolean()).getShortProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void chFail() throws JMSException { initMap(RandomData.readChar()).getShortProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void ihFail() throws JMSException { initMap(RandomData.readInt()).getShortProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void lhFail() throws JMSException { initMap(RandomData.readLong()).getShortProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void fhFail() throws JMSException { initMap(RandomData.readFloat()).getShortProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void dhFail() throws JMSException { initMap(RandomData.readDouble()).getShortProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void zhFail() throws JMSException { initMap(RandomData.readBytes(10)).getShortProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void biFail() throws JMSException { initMap(RandomData.readBoolean()).getIntProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void ciFail() throws JMSException { initMap(RandomData.readChar()).getIntProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void liFail() throws JMSException { initMap(RandomData.readLong()).getIntProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void fiFail() throws JMSException { initMap(RandomData.readFloat()).getIntProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void diFail() throws JMSException { initMap(RandomData.readDouble()).getIntProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void ziFail() throws JMSException { initMap(RandomData.readBytes(10)).getIntProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void blFail() throws JMSException { initMap(RandomData.readBoolean()).getLongProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void clFail() throws JMSException { initMap(RandomData.readChar()).getLongProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void dlFail() throws JMSException { initMap(RandomData.readDouble()).getLongProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void flFail() throws JMSException { initMap(RandomData.readFloat()).getLongProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void zlFail() throws JMSException { initMap(RandomData.readBytes(10)).getLongProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void bfFail() throws JMSException { initMap(RandomData.readBoolean()).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void yfFail() throws JMSException { initMap(RandomData.readByte()).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void hfFail() throws JMSException { initMap(RandomData.readShort()).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void cfFail() throws JMSException { initMap(RandomData.readChar()).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void ifFail() throws JMSException { initMap(RandomData.readInt()).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void lfFail() throws JMSException { initMap(RandomData.readLong()).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void dfFail() throws JMSException { initMap(RandomData.readDouble()).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void zfFail() throws JMSException { initMap(RandomData.readBytes(10)).getFloatProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void bdFail() throws JMSException { initMap(RandomData.readBoolean()).getDoubleProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void ydFail() throws JMSException { initMap(RandomData.readByte()).getDoubleProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void hdFail() throws JMSException { initMap(RandomData.readShort()).getDoubleProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void cdFail() throws JMSException { initMap(RandomData.readChar()).getDoubleProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void idFail() throws JMSException { initMap(RandomData.readInt()).getDoubleProperty("X"); }

    @Test(expected = MessageFormatException.class)
    public void ldFail() throws JMSException { initMap(RandomData.readLong()).getDoubleProperty("X"); }

    private Message initMap(Object o) throws JMSException {
        Message Message = createSession().createMessage();
        Message.setObjectProperty("X", o);
        return Message;
    }

    private class TestValues {
        final boolean bb = RandomData.readBoolean();
        final String sb = RandomData.readBoolean().toString();
        final byte yy = RandomData.readByte();
        final String sy = RandomData.readByte().toString();
        final byte yh = RandomData.readByte();
        final short hh = RandomData.readShort();
        final String sh = RandomData.readShort().toString();
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
        final int is = RandomData.readInt();
        final long ls = RandomData.readLong();
        final float fs = RandomData.readFloat();
        final double ds = RandomData.readDouble();
        final String ss = RandomData.readString();
    }
}
