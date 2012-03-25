package org.skyscreamer.nevado.jms.message;

import javax.jms.JMSException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/24/12
 * Time: 3:46 PM
 */
public class InvalidMessage extends NevadoMessage {
    private final Exception _exception;

    public InvalidMessage(Exception e) {
        super();
        _exception = e;
    }

    public Exception getException() {
        return _exception;
    }

    @Override
    public void internalClearBody() throws JMSException {
        // Nothing to do
    }
}
