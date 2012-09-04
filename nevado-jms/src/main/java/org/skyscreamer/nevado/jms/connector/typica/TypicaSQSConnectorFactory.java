package org.skyscreamer.nevado.jms.connector.typica;

import org.skyscreamer.nevado.jms.connector.AbstractSQSConnectorFactory;

import javax.jms.JMSException;

/**
 * Connectory factory for Typica connector.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TypicaSQSConnectorFactory extends AbstractSQSConnectorFactory {
    @Override
    public TypicaSQSConnector getInstance(String awsAccessKey, String awsSecretKey, String awsSQSEndpoint, String awsSNSEndpoint) throws JMSException {
        TypicaSQSConnector typicaSQSConnector = new TypicaSQSConnector(awsAccessKey, awsSecretKey, awsSQSEndpoint,
                awsSNSEndpoint, _isSecure, _receiveCheckIntervalMs);
        return typicaSQSConnector;
    }
}
