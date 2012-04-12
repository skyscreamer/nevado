package org.skyscreamer.nevado.jms.message;

import org.apache.commons.codec.binary.StringUtils;
import org.skyscreamer.nevado.jms.util.PropertyConvertUtil;
import org.skyscreamer.nevado.jms.util.SerializeUtil;

import javax.jms.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Generic non-Nevado-specific abstract message.  Designed specific to the generic specification.  Could
 * be used for other implementations.
 *
 * This abstraction was created to breakout specification-specific logic from Nevado's provider-specific
 * logic.
 *
 * @author Carter Page
 */
public abstract class AbstractMessage<T> implements Message, Serializable {
    public static final String WHITESPACE_CHARS = " \t\r\n";
    public static final String[] RESERVED_PROPERTY_NAMES = { "NULL", "TRUE", "FALSE", "NOT", "AND", "OR", "BETWEEN",
            "LIKE", "IN", "IS", "ESCAPE" };

    private final Map<String, Object> _properties = new HashMap<String, Object>();
    private String _messageID;
    private long _timestamp = 0;
    private String _correlationID;
    private Destination _jmsReplyTo;
    private Destination _destination;
    private int _deliveryMode = DEFAULT_DELIVERY_MODE;
    private boolean _redelivered = false;
    private String _type;
    private long _expiration = 0;
    private int _priority = 0;
    private boolean _readOnlyProperties = false;
    private boolean _readOnlyBody = false;

    public String getJMSMessageID() throws JMSException {
        return _messageID;
    }

    public void setJMSMessageID(String messageID) throws JMSException {
        _messageID = messageID;
    }

    public long getJMSTimestamp() throws JMSException {
        return _timestamp;
    }

    public void setJMSTimestamp(long timestamp) throws JMSException {
        _timestamp = timestamp;
    }

    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return StringUtils.getBytesUtf8(_correlationID);
    }

    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
        _correlationID = StringUtils.newStringUtf8(correlationID);
    }

    public void setJMSCorrelationID(String correlationID) throws JMSException {
        _correlationID = correlationID;
    }

    public String getJMSCorrelationID() throws JMSException {
        return _correlationID;
    }

    public Destination getJMSReplyTo() throws JMSException {
        return _jmsReplyTo;
    }

    public void setJMSReplyTo(Destination jmsReplyTo) throws JMSException {
        _jmsReplyTo = jmsReplyTo;
    }

    public Destination getJMSDestination() throws JMSException {
        return _destination;
    }

    public void setJMSDestination(Destination destination) throws JMSException {
        _destination = destination;
    }

    public int getJMSDeliveryMode() throws JMSException {
        return _deliveryMode;
    }

    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
        _deliveryMode = deliveryMode;
    }

    public boolean getJMSRedelivered() throws JMSException {
        return _redelivered;
    }

    public void setJMSRedelivered(boolean redelivered) throws JMSException {
        _redelivered = redelivered;
    }

    public String getJMSType() throws JMSException {
        return _type;
    }

    public void setJMSType(String type) throws JMSException {
        _type = type;
    }

    public long getJMSExpiration() throws JMSException {
        return _expiration;
    }

    public void setJMSExpiration(long expiration) throws JMSException {
        _expiration = expiration;
    }

    public int getJMSPriority() throws JMSException {
        return _priority;
    }

    public void setJMSPriority(int priority) throws JMSException {
        if (priority < 0 || priority > 9) {
            throw new IllegalArgumentException("Priority must be a number between 0-9.  Given: " + priority);
        }
        _priority = priority;
    }

    public void clearProperties() throws JMSException {
        _properties.clear();
        _readOnlyProperties = false;
    }

    public abstract void internalClearBody() throws JMSException;

    public void clearBody() throws JMSException {
        internalClearBody();
        _readOnlyBody = false;
    }

    public boolean propertyExists(String propertyName) throws JMSException {
        return _properties.containsKey(propertyName);
    }

    public boolean getBooleanProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToBoolean("property " + name, getObjectProperty(name));
    }

    public byte getByteProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToByte("property " + name, getObjectProperty(name));
    }

    public short getShortProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToShort("property " + name, getObjectProperty(name));
    }

    public int getIntProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToInt("property " + name, getObjectProperty(name));
    }

    public long getLongProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToLong("property " + name, getObjectProperty(name));
    }

    public float getFloatProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToFloat("property " + name, getObjectProperty(name));
    }

    public double getDoubleProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToDouble("property " + name, getObjectProperty(name));
    }

    public String getStringProperty(String name) throws JMSException {
        return PropertyConvertUtil.convertToString("property " + name, getObjectProperty(name));
    }

    public Object getObjectProperty(String name) throws JMSException {
        if (name == null) {
            throw new NullPointerException("Property name cannot be null");
        }
        return _properties.get(name);
    }

    public Enumeration getPropertyNames() throws JMSException {
        return new Vector<String>(_properties.keySet()).elements();
    }

    public void setBooleanProperty(String name, boolean value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setByteProperty(String name, byte value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setShortProperty(String name, short value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setIntProperty(String name, int value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setLongProperty(String name, long value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setFloatProperty(String name, float value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setDoubleProperty(String name, double value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setStringProperty(String name, String value) throws JMSException {
        setObjectProperty(name, value);
    }

    public void setObjectProperty(String name, Object value) throws JMSException {
        checkReadOnlyProperties();
        checkValidPropertyName(name);
        internalSetObjectProperty(name, value);
        // TODO - ActiveMQMessage uses the idea of property setter to enforce the data type for defined properties.  May be overkill.
    }

    protected void internalSetObjectProperty(String name, Object value) throws JMSException {
        if (name == null || name.trim().equals("")) {
            throw new IllegalArgumentException("Property name cannot be empty or null");
        }
        PropertyConvertUtil.checkValidObject(value);
        _properties.put(name, value);
    }
            
    public abstract void acknowledge() throws JMSException;

    private void checkReadOnlyProperties() throws MessageNotWriteableException {
        if (_readOnlyProperties) {
            throw new MessageNotWriteableException("Message properties are read-only");
        }
    }

    protected void checkReadOnlyBody() throws MessageNotWriteableException {
        if (_readOnlyBody) {
            throw new MessageNotWriteableException("Message body is read-only");
        }
    }

    // This is only used by StreamMessage, where readOnly and writeOnly are mutually exclusive.
    protected void checkWriteOnlyBody() throws MessageNotReadableException {
        if (!_readOnlyBody) {
            throw new MessageNotReadableException("Message body is write-only");
        }
    }

    private void checkValidPropertyName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Property name cannot be empty");
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            throw new IllegalArgumentException("Invalid first character: " + name.charAt(0));
        }
        if (Arrays.asList(RESERVED_PROPERTY_NAMES).contains(name)) {
            throw new IllegalArgumentException("Property name cannot be reserved word '" + name + "'");
        }
        for(char c : WHITESPACE_CHARS.toCharArray()) {
            if (name.indexOf(c) > -1) {
                throw new IllegalArgumentException("Property name cannot contain whitespace: '" + name + "'");
            }
        }
    }

    public void onSend() {
        _readOnlyProperties = true;
        _readOnlyBody = true;
    }

    public T copyOf() throws JMSException {
        try {
            byte[] data = SerializeUtil.serialize(this);
            return (T)SerializeUtil.deserialize(data);
        }
        catch(IOException e) {
            throw new JMSException("Unable to make copy of messsage: " + e.getMessage());
        }
    }
}
