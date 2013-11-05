package org.skyscreamer.nevado.jms.connector.amazonaws;

import org.apache.commons.lang.StringUtils;
import org.skyscreamer.nevado.jms.connector.AbstractSQSConnectorFactory;
import org.skyscreamer.nevado.jms.connector.CloudCredentials;

import javax.jms.ResourceAllocationException;

/**
 * Connectory factory for Amazon AWS connector.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class AmazonAwsSQSConnectorFactory extends AbstractSQSConnectorFactory {
    protected boolean _useAsyncSend = false;
    
    @Override
    public AmazonAwsSQSConnector getInstance(CloudCredentials credentials) throws ResourceAllocationException {
        if (!(credentials instanceof AmazonAwsSQSCredentials))
            throw new IllegalArgumentException("Cloud credentials must be of type AmazonAwsSQSCredentials");
        AmazonAwsSQSConnector amazonAwsSQSConnector = new AmazonAwsSQSConnector(
                (AmazonAwsSQSCredentials)credentials, _isSecure, _receiveCheckIntervalMs, _useAsyncSend);
        return amazonAwsSQSConnector;
    }
    
    public void setUseAsyncSend(boolean useAsyncSend) {
        _useAsyncSend = useAsyncSend;
    }
    
    public boolean isUseAsyncSend() {
        return _useAsyncSend;
    }
}
