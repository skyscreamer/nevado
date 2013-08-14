package org.skyscreamer.nevado.jms.connector;

import javax.jms.JMSException;

/**
 * Factory for SQSConnector objects.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public interface SQSConnectorFactory {
    SQSConnector getInstance(String awsAccessKey, String awsSecretKey) throws JMSException;
    SQSConnector getInstance(String awsAccessKey, String awsSecretKey, String awsSQSEndpoint, String awsSNSEndpoint) throws JMSException;
    SQSConnector getInstance(String awsAccessKey, String awsSecretKey, String awsSQSEndpoint, String awsSNSEndpoint, String proxyPort, String proxyHost) throws JMSException;
}
