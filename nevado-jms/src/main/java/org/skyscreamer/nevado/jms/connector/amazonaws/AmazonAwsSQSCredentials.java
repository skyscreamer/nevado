package org.skyscreamer.nevado.jms.connector.amazonaws;

import org.skyscreamer.nevado.jms.connector.CloudCredentials;

/**
 * Credentials for AmazonAWS SQS.
 *
 * User: carterp
 * Date: 11/4/13
 */
public class AmazonAwsSQSCredentials implements CloudCredentials {
    private String _awsAccessKey, _awsSecretKey, _awsSQSEndpoint, _awsSNSEndpoint;

    public AmazonAwsSQSCredentials() {}

    public AmazonAwsSQSCredentials(String awsAccessKey, String awsSecretKey) {
        _awsAccessKey = awsAccessKey;
        _awsSecretKey = awsSecretKey;
    }

    public AmazonAwsSQSCredentials(String awsAccessKey, String awsSecretKey,
                                   String awsSQSEndpoint, String awsSNSEndpoint) {
        _awsAccessKey = awsAccessKey;
        _awsSecretKey = awsSecretKey;
        _awsSQSEndpoint = awsSQSEndpoint;
        _awsSNSEndpoint = awsSNSEndpoint;
    }

    public String getAwsAccessKey() {
        return _awsAccessKey;
    }

    public void setAwsAccessKey(String awsAccessKey) {
        _awsAccessKey = awsAccessKey;
    }

    public String getAwsSecretKey() {
        return _awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        _awsSecretKey = awsSecretKey;
    }

    public String getAwsSQSEndpoint() {
        return _awsSQSEndpoint;
    }

    public void setAwsSQSEndpoint(String awsSQSEndpoint) {
        _awsSQSEndpoint = awsSQSEndpoint;
    }

    public String getAwsSNSEndpoint() {
        return _awsSNSEndpoint;
    }

    public void setAwsSNSEndpoint(String awsSNSEndpoint) {
        _awsSNSEndpoint = awsSNSEndpoint;
    }
}
