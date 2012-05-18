package org.skyscreamer.nevado.jms.connector.typica;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnectorFactory;

/**
 * Connectory factory for Typica connector.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TypicaSQSConnectorFactory implements SQSConnectorFactory {
    private final Log _log = LogFactory.getLog(getClass());

    public static final int DEFAULT_RECEIVE_CHECK_INTERVAL_MS = 200;
    private boolean _isSecure = true;
    private long _receiveCheckIntervalMs = DEFAULT_RECEIVE_CHECK_INTERVAL_MS;

    @Override
    public SQSConnector getInstance(String awsAccessKey, String awsSecretKey) {
        TypicaSQSConnector typicaSQSConnector = new TypicaSQSConnector(awsAccessKey, awsSecretKey, _isSecure,
                _receiveCheckIntervalMs);
        return typicaSQSConnector;
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
