package org.skyscreamer.nevado.jms.destination;

import org.skyscreamer.nevado.jms.destination.NevadoDestination;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.io.Serializable;
import java.net.URL;

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

    protected NevadoQueue(Queue queue) throws JMSException {
        super(queue.getQueueName());
    }

    public NevadoQueue(URL sqsURL) {
        super(sqsURL);
    }

    public String getQueueName() {
        return super.getName();
    }
}
