package org.skyscreamer.nevado.jms.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.JMSException;

/**
 * Abstract implementation of factory for SQSConnector objects.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public abstract class AbstractSQSConnectorFactory implements SQSConnectorFactory {
    protected final Log _log = LogFactory.getLog(getClass());

    public static final int DEFAULT_RECEIVE_CHECK_INTERVAL_MS = 200;
    protected boolean _isSecure = true;
    protected long _receiveCheckIntervalMs = DEFAULT_RECEIVE_CHECK_INTERVAL_MS;

    @Override
    public abstract SQSConnector getInstance(String awsAccessKey, String awsSecretKey, String awsSQSEndpoint,
                                             String awsSNSEndpoint) throws JMSException;

    @Override
    public SQSConnector getInstance(String awsAccessKey, String awsSecretKey) throws JMSException {
        return getInstance(awsAccessKey, awsSecretKey, null, null);
    }

    public void setSecure(boolean secure) {
        _isSecure = secure;
    }

    public void setReceiveCheckIntervalMs(long receiveCheckIntervalMs) {
        if (receiveCheckIntervalMs < DEFAULT_RECEIVE_CHECK_INTERVAL_MS) {
            _log.warn("Reducing the receiveCheckInterval will increase your AWS costs.  " +
                    "Amazon charges each time a check is made, even if no message is available: " +
                    "http://aws.amazon.com/sqs/pricing/");
        }
        _receiveCheckIntervalMs = receiveCheckIntervalMs;
    }
}
