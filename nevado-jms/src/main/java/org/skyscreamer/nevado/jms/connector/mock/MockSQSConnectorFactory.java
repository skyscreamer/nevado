package org.skyscreamer.nevado.jms.connector.mock;

import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnectorFactory;

/**
 * Connector factory for the mock connector.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MockSQSConnectorFactory implements SQSConnectorFactory {
    private MockSQSConnector _mockSQSConnector = new MockSQSConnector();

    @Override
    public SQSConnector getInstance(String awsAccessKey, String awsSecretKey) {
        return _mockSQSConnector;
    }
}
