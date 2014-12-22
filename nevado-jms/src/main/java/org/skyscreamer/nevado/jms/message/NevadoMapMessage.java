package org.skyscreamer.nevado.jms.message;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.skyscreamer.nevado.jms.util.CharWrapper;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 4:40 PM
 */
public class NevadoMapMessage extends NevadoMessage implements MapMessage {
    private final Map<String, Object> _map = new HashMap<String, Object>();

    public NevadoMapMessage() {}

    protected NevadoMapMessage(MapMessage message) throws JMSException {
        super(message);
        for (Enumeration keys = message.getMapNames(); keys.hasMoreElements();) {
            String key = keys.nextElement().toString();
            Object value = message.getObject(key);
            setObject(key, value);
        }
    }

    @Override
    public void internalClearBody() throws JMSException {
        _map.clear();
    }

    public boolean getBoolean(String key) throws JMSException {
        return MapMessageConvertUtil.convertToBoolean("boolean MapMessage value", _map.get(key));
    }

    public byte getByte(String key) throws JMSException {
        return MapMessageConvertUtil.convertToByte("byte MapMessage value", _map.get(key));
    }

    public short getShort(String key) throws JMSException {
        return MapMessageConvertUtil.convertToShort("short MapMessage value", _map.get(key));
    }

    public char getChar(String key) throws JMSException {
        return MapMessageConvertUtil.convertToChar("char MapMessage value", _map.get(key));
    }

    public int getInt(String key) throws JMSException {
        return MapMessageConvertUtil.convertToInt("int MapMessage value", _map.get(key));
    }

    public long getLong(String key) throws JMSException {
        return MapMessageConvertUtil.convertToLong("long MapMessage value", _map.get(key));
    }

    public float getFloat(String key) throws JMSException {
        return MapMessageConvertUtil.convertToFloat("float MapMessage value", _map.get(key));
    }

    public double getDouble(String key) throws JMSException {
        return MapMessageConvertUtil.convertToDouble("double MapMessage value", _map.get(key));
    }

    public String getString(String key) throws JMSException {
        return MapMessageConvertUtil.convertToString("string MapMessage value", _map.get(key));
    }

    public byte[] getBytes(String key) throws JMSException {
        return MapMessageConvertUtil.convertToBytes("byte[] MapMessage value", _map.get(key));
    }

    public Object getObject(String key) throws JMSException {
        Object o = _map.get(key);
        if (o instanceof CharWrapper) {
            o = ((CharWrapper)o).charValue();
        }
        if (o instanceof ByteArray) {
            o = ((ByteArray)o).toByteArray();
        }
        return o; 
    }

    public Enumeration getMapNames() throws JMSException {
        return new Vector<String>(_map.keySet()).elements();
    }

    public void setBoolean(String key, boolean value) throws JMSException {
        setObject(key, value);
    }

    public void setByte(String key, byte value) throws JMSException {
        setObject(key, value);
    }

    public void setShort(String key, short value) throws JMSException {
        setObject(key, value);
    }

    public void setChar(String key, char value) throws JMSException {
        setObject(key, value);
    }

    public void setInt(String key, int value) throws JMSException {
        setObject(key, value);
    }

    public void setLong(String key, long value) throws JMSException {
        setObject(key, value);
    }

    public void setFloat(String key, float value) throws JMSException {
        setObject(key, value);
    }

    public void setDouble(String key, double value) throws JMSException {
        setObject(key, value);
    }

    public void setString(String key, String value) throws JMSException {
        setObject(key, value);
    }

    public void setBytes(String key, byte[] value) throws JMSException {
        setObject(key, value);
    }

    public void setBytes(String key, byte[] value, int offset, int length) throws JMSException {
        setObject(key, value);
    }

    public void setObject(String key, Object value) throws JMSException {
        checkReadOnlyBody();
        if (key == null || key.trim().equals("")) {
            throw new IllegalArgumentException("MapMessage key cannot be empty or null");
        }
        if (value instanceof byte[]) {
            value = new ByteArray((byte[])value);
        }
        if (value instanceof Character) {
            value = new CharWrapper((Character)value);
        }
        MapMessageConvertUtil.checkValidObject(value);
        _map.put(key, value);
    }

    public boolean itemExists(String key) throws JMSException {
        return _map.containsKey(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NevadoMapMessage that = (NevadoMapMessage) o;

        if (_messageID != null ? !_messageID.equals(that._messageID) : that._messageID != null) return false;
        if (_map != null ? !_map.equals(that._map) : that._map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_messageID).append(_map).toHashCode();
    }
}
