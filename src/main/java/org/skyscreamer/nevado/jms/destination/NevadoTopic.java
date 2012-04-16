package org.skyscreamer.nevado.jms.destination;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.Serializable;
import java.net.URL;

/**
 * Nevado implementation of a topic
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoTopic extends NevadoDestination implements Topic {
    private String _arn;

    public NevadoTopic(String name) {
        super(name);
    }

    protected NevadoTopic(NevadoDestination destination) {
        super(destination);
    }

    public NevadoTopic(URL sqsURL) {
        super(sqsURL);
    }

    public String getTopicName() {
        return getName();
    }

    public String getArn() {
        return _arn;
    }

    public void setArn(String arn) {
        _arn = arn;
    }
}
