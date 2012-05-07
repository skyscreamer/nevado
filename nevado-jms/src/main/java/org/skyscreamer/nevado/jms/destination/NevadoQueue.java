package org.skyscreamer.nevado.jms.destination;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/22/12
 * Time: 3:35 AM
 */
public class NevadoQueue extends NevadoDestination implements Queue {
    private String _queueUrl;

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

    public String getQueueUrl() {
        return _queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        _queueUrl = queueUrl;
    }
}
