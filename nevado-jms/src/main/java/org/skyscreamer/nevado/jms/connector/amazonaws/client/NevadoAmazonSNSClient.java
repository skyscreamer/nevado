package org.skyscreamer.nevado.jms.connector.amazonaws.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.UnsubscribeRequest;

public class NevadoAmazonSNSClient implements NevadoAmazonSNS {

    private AmazonSNS _client;

    public NevadoAmazonSNSClient(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration) {
        _client = new AmazonSNSClient(awsCredentials, clientConfiguration);
    }

    @Override
    public void setEndpoint(String endpoint) throws IllegalArgumentException {
        _client.setEndpoint(endpoint);
    }

    @Override
    public SubscribeResult subscribe(SubscribeRequest subscribeRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.subscribe(subscribeRequest);
    }

    @Override
    public void deleteTopic(DeleteTopicRequest deleteTopicRequest) throws AmazonServiceException, AmazonClientException {
        _client.deleteTopic(deleteTopicRequest);
    }

    @Override
    public CreateTopicResult createTopic(CreateTopicRequest createTopicRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.createTopic(createTopicRequest);
    }

    @Override
    public void unsubscribe(UnsubscribeRequest unsubscribeRequest) throws AmazonServiceException, AmazonClientException {
        _client.unsubscribe(unsubscribeRequest);
    }

    @Override
    public PublishResult publish(PublishRequest publishRequest) throws AmazonServiceException, AmazonClientException {
        return _client.publish(publishRequest);
    }

    @Override
    public ListSubscriptionsResult listSubscriptions() throws AmazonServiceException, AmazonClientException {
        return _client.listSubscriptions();
    }

    @Override
    public ListTopicsResult listTopics() throws AmazonServiceException, AmazonClientException {
        return _client.listTopics();
    }

}
