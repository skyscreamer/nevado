package org.skyscreamer.nevado.jms.message;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.skyscreamer.nevado.jms.util.SerializeUtil;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 11:14 AM
 */
public class NevadoObjectMessage extends NevadoMessage implements ObjectMessage {
    private byte[] _bodyBytes;

    public NevadoObjectMessage() {}

    protected NevadoObjectMessage(ObjectMessage message) throws JMSException {
        super(message);
        Serializable serializable = message.getObject();
        setBodyObject(serializable);
    }

    public void setObject(Serializable serializable) throws JMSException {
        checkReadOnlyBody();
        setBodyObject(serializable);
    }

    public Serializable getObject() throws JMSException {
        return getBodyObject();
    }

    @Override
    public void internalClearBody() throws JMSException {
        _bodyBytes = null;
    }

    private void setBodyObject(Serializable serializable) throws JMSException {
        if (serializable == null) {
            _bodyBytes = null;
        }
        else {
            try {
                _bodyBytes = SerializeUtil.serialize(serializable);
            } catch (IOException e) {
                throw new JMSException("Unable to serialize body object");
            }
        }
    }

    private Serializable getBodyObject() throws JMSException {
        if (_bodyBytes == null) {
            return null;
        }
        else {
            try {
                return SerializeUtil.deserialize(_bodyBytes);
            } catch (IOException e) {
                throw new JMSException("Unable to deserialize body object");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NevadoObjectMessage that = (NevadoObjectMessage) o;

        if (_messageID != null ? !_messageID.equals(that._messageID) : that._messageID != null) return false;
        if (_bodyBytes != null ? !_bodyBytes.equals(that._bodyBytes) : that._bodyBytes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_messageID).append(_bodyBytes).toHashCode();
    }
}
