package org.skyscreamer.nevado.jms.connector.amazonaws;

import org.apache.commons.lang.StringUtils;
import org.skyscreamer.nevado.jms.connector.AbstractSQSConnectorFactory;

/**
 * Connectory factory for Amazon AWS connector.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class AmazonAwsSQSConnectorFactory extends AbstractSQSConnectorFactory {
    protected boolean _useAsyncSend = false;
    
    @Override
    public AmazonAwsSQSConnector getInstance(String awsAccessKey, String awsSecretKey, String awsSessionToken, String awsSQSEndpoint, String awsSNSEndpoint) {
        AmazonAwsSQSConnector amazonAwsSQSConnector = new AmazonAwsSQSConnector(awsAccessKey, awsSecretKey, awsSessionToken, _isSecure,
                _receiveCheckIntervalMs, _useAsyncSend);
        if (StringUtils.isNotEmpty(awsSQSEndpoint)) {
            amazonAwsSQSConnector.getAmazonSQS().setEndpoint(awsSQSEndpoint);
        }
        if (StringUtils.isNotEmpty(awsSNSEndpoint)) {
            amazonAwsSQSConnector.getAmazonSNS().setEndpoint(awsSNSEndpoint);
        }
        return amazonAwsSQSConnector;
    }
    
    public void setUseAsyncSend(boolean useAsyncSend) {
        _useAsyncSend = useAsyncSend;
    }
    
    public boolean isUseAsyncSend() {
        return _useAsyncSend;
    }
}
