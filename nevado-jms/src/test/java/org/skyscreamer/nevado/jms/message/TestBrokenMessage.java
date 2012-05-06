package org.skyscreamer.nevado.jms.message;

import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.message.JMSXProperty;
import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoProperty;

import javax.jms.*;
import java.util.Enumeration;

/**
 * Bomb message.  Used to test recovery when things blow up in unexpected ways.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TestBrokenMessage extends NevadoMessage {
    private final NevadoMessage _delegatee;

    public TestBrokenMessage(NevadoMessage delegatee)
    {
        _delegatee = delegatee;
    }

    @Override
    public NevadoSession getNevadoSession() {
        return _delegatee.getNevadoSession();
    }

    @Override
    public void setNevadoSession(NevadoSession nevadoSession) {
        _delegatee.setNevadoSession(nevadoSession);
    }

    @Override
    public NevadoDestination getNevadoDestination() {
        return _delegatee.getNevadoDestination();
    }

    @Override
    public void setNevadoDestination(NevadoDestination nevadoDestination) {
        _delegatee.setNevadoDestination(nevadoDestination);
    }

    @Override
    public boolean nevadoPropertyExists(NevadoProperty property) throws JMSException {
        return _delegatee.nevadoPropertyExists(property);
    }

    @Override
    public Object getNevadoProperty(NevadoProperty nevadoProperty) throws JMSException {
        return _delegatee.getNevadoProperty(nevadoProperty);
    }

    @Override
    public void setNevadoProperty(NevadoProperty nevadoProperty, Object value) throws JMSException {
        throw new RuntimeException("TEST - Boom!");
    }

    @Override
    public void acknowledge() throws JMSException {
        _delegatee.acknowledge();
    }

    @Override
    public void expire() throws JMSException {
        _delegatee.expire();
    }

    public static NevadoMessage getInstance(Message message) throws JMSException {
        return NevadoMessage.getInstance(message);
    }

    @Override
    public void setJMSXProperty(JMSXProperty property, Object value) throws JMSException {
        _delegatee.setJMSXProperty(property, value);
    }

    @Override
    public Object getJMSXProperty(JMSXProperty property) throws JMSException {
        return _delegatee.getJMSXProperty(property);
    }

    @Override
    public boolean isAcknowledged() {
        return _delegatee.isAcknowledged();
    }

    @Override
    public void setAcknowledged(boolean acknowledged) {
        _delegatee.setAcknowledged(acknowledged);
    }

    @Override
    public boolean isDisableMessageID() {
        return _delegatee.isDisableMessageID();
    }

    @Override
    public void setDisableMessageID(boolean _disableMessageID) {
        _delegatee.setDisableMessageID(_disableMessageID);
    }

    @Override
    public boolean isDisableTimestamp() {
        return _delegatee.isDisableTimestamp();
    }

    @Override
    public void setDisableTimestamp(boolean _disableTimestamp) {
        _delegatee.setDisableTimestamp(_disableTimestamp);
    }

    @Override
    public String getJMSMessageID() throws JMSException {
        return _delegatee.getJMSMessageID();
    }

    @Override
    public void setJMSMessageID(String messageID) throws JMSException {
        _delegatee.setJMSMessageID(messageID);
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        return _delegatee.getJMSTimestamp();
    }

    @Override
    public void setJMSTimestamp(long timestamp) throws JMSException {
        throw new RuntimeException("TEST - Boom!");
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return _delegatee.getJMSCorrelationIDAsBytes();
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
        _delegatee.setJMSCorrelationIDAsBytes(correlationID);
    }

    @Override
    public void setJMSCorrelationID(String correlationID) throws JMSException {
        _delegatee.setJMSCorrelationID(correlationID);
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        return _delegatee.getJMSCorrelationID();
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        return _delegatee.getJMSReplyTo();
    }

    @Override
    public void setJMSReplyTo(Destination jmsReplyTo) throws JMSException {
        _delegatee.setJMSReplyTo(jmsReplyTo);
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        return _delegatee.getJMSDestination();
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException {
        _delegatee.setJMSDestination(destination);
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        return _delegatee.getJMSDeliveryMode();
    }

    @Override
    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
        _delegatee.setJMSDeliveryMode(deliveryMode);
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        return _delegatee.getJMSRedelivered();
    }

    @Override
    public void setJMSRedelivered(boolean redelivered) throws JMSException {
        _delegatee.setJMSRedelivered(redelivered);
    }

    @Override
    public String getJMSType() throws JMSException {
        return _delegatee.getJMSType();
    }

    @Override
    public void setJMSType(String type) throws JMSException {
        _delegatee.setJMSType(type);
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        return _delegatee.getJMSExpiration();
    }

    @Override
    public void setJMSExpiration(long expiration) throws JMSException {
        _delegatee.setJMSExpiration(expiration);
    }

    @Override
    public int getJMSPriority() throws JMSException {
        return _delegatee.getJMSPriority();
    }

    @Override
    public void setJMSPriority(int priority) throws JMSException {
        _delegatee.setJMSPriority(priority);
    }

    @Override
    public void clearProperties() throws JMSException {
        _delegatee.clearProperties();
    }

    @Override
    public void internalClearBody() throws JMSException {
        _delegatee.internalClearBody();
    }

    @Override
    public void clearBody() throws JMSException {
        _delegatee.clearBody();
    }

    @Override
    public boolean propertyExists(String propertyName) throws JMSException {
        return _delegatee.propertyExists(propertyName);
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException {
        return _delegatee.getBooleanProperty(name);
    }

    @Override
    public byte getByteProperty(String name) throws JMSException {
        return _delegatee.getByteProperty(name);
    }

    @Override
    public short getShortProperty(String name) throws JMSException {
        return _delegatee.getShortProperty(name);
    }

    @Override
    public int getIntProperty(String name) throws JMSException {
        return _delegatee.getIntProperty(name);
    }

    @Override
    public long getLongProperty(String name) throws JMSException {
        return _delegatee.getLongProperty(name);
    }

    @Override
    public float getFloatProperty(String name) throws JMSException {
        return _delegatee.getFloatProperty(name);
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException {
        return _delegatee.getDoubleProperty(name);
    }

    @Override
    public String getStringProperty(String name) throws JMSException {
        return _delegatee.getStringProperty(name);
    }

    @Override
    public Object getObjectProperty(String name) throws JMSException {
        return _delegatee.getObjectProperty(name);
    }

    @Override
    public Enumeration getPropertyNames() throws JMSException {
        return _delegatee.getPropertyNames();
    }

    @Override
    public void setBooleanProperty(String name, boolean value) throws JMSException {
        _delegatee.setBooleanProperty(name, value);
    }

    @Override
    public void setByteProperty(String name, byte value) throws JMSException {
        _delegatee.setByteProperty(name, value);
    }

    @Override
    public void setShortProperty(String name, short value) throws JMSException {
        _delegatee.setShortProperty(name, value);
    }

    @Override
    public void setIntProperty(String name, int value) throws JMSException {
        _delegatee.setIntProperty(name, value);
    }

    @Override
    public void setLongProperty(String name, long value) throws JMSException {
        _delegatee.setLongProperty(name, value);
    }

    @Override
    public void setFloatProperty(String name, float value) throws JMSException {
        _delegatee.setFloatProperty(name, value);
    }

    @Override
    public void setDoubleProperty(String name, double value) throws JMSException {
        _delegatee.setDoubleProperty(name, value);
    }

    @Override
    public void setStringProperty(String name, String value) throws JMSException {
        _delegatee.setStringProperty(name, value);
    }

    @Override
    public void setObjectProperty(String name, Object value) throws JMSException {
        _delegatee.setObjectProperty(name, value);
    }

    @Override
    protected void internalSetObjectProperty(String name, Object value) throws JMSException {
        _delegatee.internalSetObjectProperty(name, value);
    }

    @Override
    protected void checkReadOnlyBody() throws MessageNotWriteableException {
        _delegatee.checkReadOnlyBody();
    }

    @Override
    protected void checkWriteOnlyBody() throws MessageNotReadableException {
        _delegatee.checkWriteOnlyBody();
    }

    @Override
    public void onSend() {
        _delegatee.onSend();
    }
}
