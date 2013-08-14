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
 * @author Andy Savin <andy.savin@vistair.com>
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
                String proxyHost = getRefContent(ref, NevadoConnectionFactory.JNDI_PROXY_HOST);
                if (proxyHost != null)
                {
                    connectionFactory.setProxyHost(proxyHost);
                }
                String proxyPort = getRefContent(ref, NevadoConnectionFactory.JNDI_PROXY_PORT);
                if (proxyPort != null)
                {
                    connectionFactory.setProxyPort(proxyPort);
                }
                String sqsConnectorFactoryClass = getRefContent(ref, NevadoConnectionFactory.JNDI_SQS_CONNECTOR_FACTORY_CLASS);
	            if (sqsConnectorFactoryClass != null) {
	            	SQSConnectorFactory sqsConnectorFactory = (SQSConnectorFactory) Class.forName(sqsConnectorFactoryClass).newInstance();
	                connectionFactory.setSqsConnectorFactory(sqsConnectorFactory);
	            }
                instance = connectionFactory;
            }
            else if (ref.getClassName().equals(NevadoQueue.class.getName())) {
                instance = new NevadoQueue(getRefContent(ref, NevadoDestination.JNDI_DESTINATION_NAME));
            }
            else if (ref.getClassName().equals(NevadoTopic.class.getName())) {
                instance = new NevadoTopic(getRefContent(ref, NevadoDestination.JNDI_DESTINATION_NAME));
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
