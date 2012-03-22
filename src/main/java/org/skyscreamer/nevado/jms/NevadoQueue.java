package org.skyscreamer.nevado.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/22/12
 * Time: 3:35 AM
 */
public class NevadoQueue implements Queue {
    private final String _name;

    public NevadoQueue(String name) {
        _name = name;
    }

    public String getQueueName() throws JMSException {
        return _name;
    }
}
