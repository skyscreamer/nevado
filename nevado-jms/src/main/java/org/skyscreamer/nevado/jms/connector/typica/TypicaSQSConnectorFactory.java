package org.skyscreamer.nevado.jms.connector.typica;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.connector.AbstractSQSConnectorFactory;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnectorFactory;

/**
 * Connectory factory for Typica connector.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TypicaSQSConnectorFactory extends AbstractSQSConnectorFactory {
    @Override
    public TypicaSQSConnector getInstance(String awsAccessKey, String awsSecretKey) {
        TypicaSQSConnector typicaSQSConnector = new TypicaSQSConnector(awsAccessKey, awsSecretKey, _isSecure,
                _receiveCheckIntervalMs);
        return typicaSQSConnector;
    }
}
