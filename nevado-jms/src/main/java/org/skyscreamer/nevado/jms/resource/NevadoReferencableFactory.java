package org.skyscreamer.nevado.jms.resource;

import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.SQSConnectorFactory;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * This is the factory for JNDI referenceable objects.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoReferencableFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context ctx, Hashtable env) throws Exception
    {
        Object instance;
        if (obj instanceof Reference) {
            Reference ref = (Reference)obj;
            if (ref.getClassName().equals(NevadoConnectionFactory.class.getName())) {
                NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory();
                connectionFactory.setAwsAccessKey(getRefContent(ref, NevadoConnectionFactory.JNDI_AWS_ACCESS_KEY));
                connectionFactory.setAwsSecretKey(getRefContent(ref, NevadoConnectionFactory.JNDI_AWS_SECRET_KEY));
                String clientId = getRefContent(ref, NevadoConnectionFactory.JNDI_CLIENT_ID);
                if (clientId != null)
                {
                    connectionFactory.setClientID(clientId);
                }
                String jmsDeliveryMode = getRefContent(ref, NevadoConnectionFactory.JNDI_JMS_DELIVERY_MODE);
                if (jmsDeliveryMode != null)
                {
                    connectionFactory.setOverrideJMSDeliveryMode(Integer.parseInt(jmsDeliveryMode));
                }
                String jmsPriority = getRefContent(ref, NevadoConnectionFactory.JNDI_JMS_PRIORITY);
                if (jmsPriority != null)
                {
                    connectionFactory.setOverrideJMSPriority(Integer.parseInt(jmsPriority));
                }
                String jmsTtl = getRefContent(ref, NevadoConnectionFactory.JNDI_JMS_TTL);
                if (jmsTtl != null)
                {
                    connectionFactory.setOverrideJMSTTL(Long.parseLong(jmsTtl));
                }
                String sqsEndpoint = getRefContent(ref, NevadoConnectionFactory.JNDI_SQS_ENDPOINT);
                if (sqsEndpoint != null)
                {
                    connectionFactory.setAwsSQSEndpoint(sqsEndpoint);
                }
                String snsEndPoint = getRefContent(ref, NevadoConnectionFactory.JNDI_SNS_ENDPOINT);
                if (snsEndPoint != null)
                {
                    connectionFactory.setAwsSNSEndpoint(snsEndPoint);
                }
                String maxPollWaitMs = getRefContent(ref, NevadoConnectionFactory.JNDI_MAX_POLL_WAIT_MS);
                if (maxPollWaitMs != null)
                {
                    connectionFactory.setMaxPollWaitMs(Long.valueOf(maxPollWaitMs));
                }
                String durableSubscriberPrefixOverride = getRefContent(ref, NevadoConnectionFactory.JNDI_DURABLE_SUBSCRIBER_PREFIX_OVERRIDE);
                if (durableSubscriberPrefixOverride != null)
                {
                    connectionFactory.setDurableSubscriberPrefixOverride(durableSubscriberPrefixOverride);
                }
                String sqsConnectorFactoryClass = getRefContent(ref, NevadoConnectionFactory.JNDI_SQS_CONNECTOR_FACTORY_CLASS);
                if (sqsConnectorFactoryClass != null)
                {
                    SQSConnectorFactory sqsConnectorFactory = (SQSConnectorFactory) Class.forName(sqsConnectorFactoryClass).newInstance();
                    connectionFactory.setSqsConnectorFactory(sqsConnectorFactory);
                }
                instance = connectionFactory;
            }
            else if (ref.getClassName().equals(NevadoQueue.class.getName())) {
                NevadoQueue queue = new NevadoQueue(getRefContent(ref, NevadoDestination.JNDI_DESTINATION_NAME));
                String queueUrl = getRefContent(ref, NevadoQueue.JNDI_QUEUE_URL);
                if (queueUrl != null) {
                    queue.setQueueUrl(queueUrl);
                }
                instance = queue;
            }
            else if (ref.getClassName().equals(NevadoTopic.class.getName())) {
                NevadoTopic topic = new NevadoTopic(getRefContent(ref, NevadoDestination.JNDI_DESTINATION_NAME));
                String arn = getRefContent(ref, NevadoTopic.JNDI_TOPIC_ARN);
                if (arn != null) {
                    topic.setArn(arn);
                }
                String subscriptionArn = getRefContent(ref, NevadoTopic.JDNI_TOPIC_SUBSCRIPTION_ARN);
                NevadoQueue endpoint = null;
                String durableString = getRefContent(ref, NevadoTopic.JNDI_TOPIC_DURABLE);
                boolean durable = false;
                if (durableString != null) {
                    durable = Boolean.parseBoolean(durableString);
                }
                String endpointName = getRefContent(ref, NevadoTopic.JNDI_TOPIC_ENDPOINT_NAME);
                if (endpointName != null) {
                    endpoint = new NevadoQueue(endpointName);
                    String endpointUrl = getRefContent(ref, NevadoTopic.JNDI_TOPIC_ENDPOINT_URL);
                    if (endpointUrl != null) {
                        endpoint.setQueueUrl(endpointUrl);
                    }
                }
                topic = new NevadoTopic(topic, endpoint, subscriptionArn, durable);
                instance = topic;
            }
            else {
                throw new IllegalArgumentException("This factory does not support objects of type "
                        + ref.getClassName());
            }
        }
        else
        {
            throw new IllegalArgumentException("Expected object of type Reference");
        }

        return instance;
    }

    private String getRefContent(Reference ref, String type) {
        RefAddr addr = ref.get(type);
        String content = addr != null ? ((String)addr.getContent()) : null;
        return content;
    }
}
