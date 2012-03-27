package org.skyscreamer.nevado.jms.message;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/26/12
 * Time: 11:19 PM
 */
public class NevadoBlankMessage extends NevadoMessage implements Message {
    @Override
    public void internalClearBody() throws JMSException {
        // Do nothing
    }
}
