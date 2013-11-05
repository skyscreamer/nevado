package org.skyscreamer.nevado.jms.resource;

import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.CloudCredentials;
import org.skyscreamer.nevado.jms.destination.NevadoDestination;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.util.SerializeUtil;

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
                byte[] serializedData = getBinaryRefContent(ref, NevadoConnectionFactory.JNDI_CLOUD_CREDENTIALS);
                connectionFactory.setCloudCredentials((CloudCredentials)SerializeUtil.deserialize(serializedData));
                String clientId = getStringRefContent(ref, NevadoConnectionFactory.JNDI_CLIENT_ID);
                if (clientId != null)
                {
                    connectionFactory.setClientID(clientId);
                }
                String jmsDeliveryMode = getStringRefContent(ref, NevadoConnectionFactory.JNDI_JMS_DELIVERY_MODE);
                if (jmsDeliveryMode != null)
                {
                    connectionFactory.setOverrideJMSDeliveryMode(Integer.parseInt(jmsDeliveryMode));
                }
                String jmsPriority = getStringRefContent(ref, NevadoConnectionFactory.JNDI_JMS_PRIORITY);
                if (jmsPriority != null)
                {
                    connectionFactory.setOverrideJMSPriority(Integer.parseInt(jmsPriority));
                }
                String jmsTtl = getStringRefContent(ref, NevadoConnectionFactory.JNDI_JMS_TTL);
                if (jmsTtl != null)
                {
                    connectionFactory.setOverrideJMSTTL(Long.parseLong(jmsTtl));
                }
                instance = connectionFactory;
            }
            else if (ref.getClassName().equals(NevadoQueue.class.getName())) {
                instance = new NevadoQueue(getStringRefContent(ref, NevadoDestination.JNDI_DESTINATION_NAME));
            }
            else if (ref.getClassName().equals(NevadoTopic.class.getName())) {
                instance = new NevadoTopic(getStringRefContent(ref, NevadoDestination.JNDI_DESTINATION_NAME));
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

    private String getStringRefContent(Reference ref, String type) {
        RefAddr addr = ref.get(type);
        String content = addr != null ? ((String)addr.getContent()) : null;
        return content;
    }

    private byte[] getBinaryRefContent(Reference ref, String type) {
        RefAddr addr = ref.get(type);
        byte[] content = addr != null ? ((byte[])addr.getContent()) : null;
        return content;
    }
}
