package org.skyscreamer.nevado.jms.connector.amazonaws;

import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSConnector;
import org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSConnectorFactory;

/**
 * Produces {@link PlainTextAmazonSQSConnector}
 * 
 * @author qi.chen
 */
public class PlainTextAmazonSQSConnectorFactory extends AmazonAwsSQSConnectorFactory {

    @Override
    protected AmazonAwsSQSConnector createConnector(String awsAccessKey, String awsSecretKey) {
        return new PlainTextAmazonSQSConnector(awsAccessKey, awsSecretKey, _isSecure, _receiveCheckIntervalMs,
                _useAsyncSend);
    }

}
