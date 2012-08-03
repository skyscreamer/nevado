package org.skyscreamer.nevado.jms.connector.amazonaws;

import org.skyscreamer.nevado.jms.connector.AbstractSQSConnectorFactory;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnectorFactory;
import org.skyscreamer.nevado.jms.connector.typica.TypicaSQSConnector;

/**
 * Connectory factory for Amazon AWS connector.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class AmazonAwsSQSConnectorFactory extends AbstractSQSConnectorFactory {
    @Override
    public AmazonAwsSQSConnector getInstance(String awsAccessKey, String awsSecretKey) {
        AmazonAwsSQSConnector amazonAwsSQSConnector = new AmazonAwsSQSConnector(awsAccessKey, awsSecretKey, _isSecure,
                _receiveCheckIntervalMs);
        return amazonAwsSQSConnector;
    }
}
