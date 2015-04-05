package org.skyscreamer.nevado.jms.destination;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/22/12
 * Time: 3:35 AM
 */
public class NevadoQueue extends NevadoDestination implements Queue {
    public static final String JNDI_QUEUE_URL = "queueUrl";

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

    @Override
    protected void addStringRefAddrs(Reference reference) {
        if (_queueUrl != null) {
            reference.add(new StringRefAddr(JNDI_QUEUE_URL, _queueUrl));
        }
    }
}
