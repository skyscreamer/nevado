package org.skyscreamer.nevado.jms.connector;

/**
 * Factory for SQSConnector objects.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public interface SQSConnectorFactory {
    SQSConnector getInstance(String awsAccessKey, String awsSecretKey);
}
