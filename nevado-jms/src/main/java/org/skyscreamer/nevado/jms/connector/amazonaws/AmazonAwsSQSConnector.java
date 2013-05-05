package org.skyscreamer.nevado.jms.connector.amazonaws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

/**
 * Connector for SQS-only implementation of the Nevado JMS driver.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class AmazonAwsSQSConnector extends AbstractAmazonAwsSQSConnector {
    protected final AmazonSQS _amazonSQS;
    protected final AmazonSNS _amazonSNS;

    public AmazonAwsSQSConnector(String awsAccessKey, String awsSecretKey, boolean isSecure, long receiveCheckIntervalMs) {
        super(receiveCheckIntervalMs);
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(isSecure ? Protocol.HTTPS : Protocol.HTTP);
        _amazonSQS = new AmazonSQSClient(awsCredentials, clientConfiguration);
        _amazonSNS = new AmazonSNSClient(awsCredentials, clientConfiguration);
    }

    @Override
    public AmazonSQS getAmazonSQS() {
        return _amazonSQS;
    }

    @Override
    public AmazonSNS getAmazonSNS() {
        return _amazonSNS;
    }

}
