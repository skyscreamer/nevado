package org.skyscreamer.nevado.jms.resource;

import org.skyscreamer.nevado.jms.NevadoConnectionFactory;

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
                connectionFactory.setAwsSecretKey(getRefContent(ref, NevadoConnectionFactory.JNDI_AWS_ACCESS_KEY));
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
                instance = connectionFactory;
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
