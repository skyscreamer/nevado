package org.skyscreamer.nevado.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/22/12
 * Time: 3:35 AM
 */
public class NevadoQueue extends NevadoDestination implements Queue {
    public NevadoQueue(String name) {
        super(name);
    }

    public String getQueueName() throws JMSException {
        return super.getName();
    }
}
