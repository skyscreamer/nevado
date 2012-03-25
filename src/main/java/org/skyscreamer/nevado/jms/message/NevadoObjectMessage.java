package org.skyscreamer.nevado.jms.message;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 11:14 AM
 */
public class NevadoObjectMessage extends NevadoMessage implements ObjectMessage {
    private Serializable _body;

    public void setObject(Serializable object) throws JMSException {
        checkReadOnlyBody();
        _body = object;
    }

    public Serializable getObject() throws JMSException {
        return _body;
    }

    @Override
    public void internalClearBody() throws JMSException {
        _body = null;
    }
}
