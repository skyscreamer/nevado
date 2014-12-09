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

    private boolean _testAlwaysPasses = false;

    @Override
    public AmazonAwsSQSConnector getInstance(String awsAccessKey, String awsSecretKey, String awsSQSEndpoint, String awsSNSEndpoint) {
        AmazonAwsSQSConnector amazonAwsSQSConnector = createConnector(awsAccessKey, awsSecretKey);
        amazonAwsSQSConnector.setTestAlwaysPasses(_testAlwaysPasses);
        if (StringUtils.isNotEmpty(awsSQSEndpoint)) {
            amazonAwsSQSConnector.getAmazonSQS().setEndpoint(awsSQSEndpoint);
        }
        if (StringUtils.isNotEmpty(awsSNSEndpoint)) {
            amazonAwsSQSConnector.getAmazonSNS().setEndpoint(awsSNSEndpoint);
        }
        return amazonAwsSQSConnector;
    }

    protected AmazonAwsSQSConnector createConnector(String awsAccessKey, String awsSecretKey) {
        return new AmazonAwsSQSConnector(awsAccessKey, awsSecretKey, _isSecure, _receiveCheckIntervalMs, _useAsyncSend);
    }

    public void setUseAsyncSend(boolean useAsyncSend) {
        _useAsyncSend = useAsyncSend;
    }

    public boolean isUseAsyncSend() {
        return _useAsyncSend;
    }

    public void setTestAlwaysPasses(boolean _testAlwaysPasses) {
        this._testAlwaysPasses = _testAlwaysPasses;
    }

}
