package org.skyscreamer.nevado.jms;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/22/12
 * Time: 3:35 AM
 */
public class NevadoQueue extends NevadoDestination implements Queue, Serializable {
    private String x;
    public NevadoQueue(String name) {
        super(name);
    }

    public String getQueueName() throws JMSException {
        return super.getName();
    }
}
