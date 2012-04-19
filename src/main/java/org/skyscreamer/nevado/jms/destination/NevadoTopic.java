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
    private final NevadoQueue _topicEndpoint;
    private final String _subscriptionArn;
    private final boolean _durable;

    public NevadoTopic(String name) {
        super(name.startsWith("arn:") ? name.substring(name.lastIndexOf(":") + 1) : name);
        _topicEndpoint = null;
        _subscriptionArn = null;
        _durable = false;
    }

    protected NevadoTopic(NevadoTopic topic) {
        super(topic);
        _topicEndpoint = null;
        _subscriptionArn = null;
        _durable = false;
    }

    public NevadoTopic(NevadoTopic topic, NevadoQueue topicEndpoint, String subscriptionArn, boolean durable)
    {
        super(topic);
        _arn = topic.getArn();
        _topicEndpoint = topicEndpoint;
        _subscriptionArn = subscriptionArn;
        _durable = durable;
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

    public NevadoQueue getTopicEndpoint() {
        return _topicEndpoint;
    }

    public String getSubscriptionArn() {
        return _subscriptionArn;
    }

    public boolean isDurable() {
        return _durable;
    }
}
