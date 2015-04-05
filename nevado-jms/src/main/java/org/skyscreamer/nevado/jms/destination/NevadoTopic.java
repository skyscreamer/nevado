package org.skyscreamer.nevado.jms.destination;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

/**
 * Nevado implementation of a topic
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoTopic extends NevadoDestination implements Topic {
    public static final String JNDI_TOPIC_ARN = "arn";
    public static final String JDNI_TOPIC_SUBSCRIPTION_ARN = "subscriptionArn";
    public static final String JNDI_TOPIC_DURABLE = "durable";
    public static final String JNDI_TOPIC_ENDPOINT_NAME = "endpointName";
    public static final String JNDI_TOPIC_ENDPOINT_URL = "endpointUrl";

    private volatile String _arn;
    private final NevadoQueue _topicEndpoint;
    private final String _subscriptionArn;
    private final boolean _durable;

    public NevadoTopic(String name) {
        super(name.startsWith("arn:") ? name.substring(name.lastIndexOf(":") + 1) : name);
        _topicEndpoint = null;
        _subscriptionArn = null;
        _durable = false;
    }

    protected NevadoTopic(Topic topic) throws JMSException {
        super(topic.getTopicName());
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

    public static NevadoTopic getInstance(Topic topic) throws JMSException {
        NevadoTopic nevadoTopic = null;

        if (topic != null) {
            if (topic instanceof NevadoTopic) {
                nevadoTopic = (NevadoTopic) topic;
            }
            else if (topic instanceof TemporaryTopic) {
                throw new IllegalStateException("TemporaryDestinations cannot be copied");
            }
            else if (topic instanceof Topic) {
                nevadoTopic = new NevadoTopic(topic);
            }
        }

        return nevadoTopic;
    }

    @Override
    protected void addStringRefAddrs(Reference reference) {
        if (_arn != null) {
            reference.add(new StringRefAddr(JNDI_TOPIC_ARN, _arn));
        }
        if (_subscriptionArn != null) {
            reference.add(new StringRefAddr(JDNI_TOPIC_SUBSCRIPTION_ARN, _subscriptionArn));
        }
        reference.add(new StringRefAddr(JNDI_TOPIC_DURABLE, Boolean.toString(_durable)));
        if (_topicEndpoint != null) {
            reference.add(new StringRefAddr(JNDI_TOPIC_ENDPOINT_NAME, _topicEndpoint.getQueueName()));
            _topicEndpoint.getQueueName();
            String queueUrl = _topicEndpoint.getQueueUrl();
            if (queueUrl != null) {
                reference.add(new StringRefAddr(JNDI_TOPIC_ENDPOINT_URL, queueUrl));
            }
        }
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
