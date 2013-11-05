package org.skyscreamer.nevado.jms.connector;

import javax.jms.JMSException;

/**
 * Factory for SQSConnector objects.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public interface SQSConnectorFactory {
    SQSConnector getInstance(CloudCredentials credentials) throws JMSException;
}
