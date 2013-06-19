package org.skyscreamer.nevado.jms.connector.amazonaws.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.AddPermissionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.GetTopicAttributesRequest;
import com.amazonaws.services.sns.model.GetTopicAttributesResult;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.ListSubscriptionsRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsResult;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.RemovePermissionRequest;
import com.amazonaws.services.sns.model.SetTopicAttributesRequest;
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
    public void shutdown() {
        _client.shutdown();
    }

    @Override
    public ConfirmSubscriptionResult confirmSubscription(ConfirmSubscriptionRequest confirmSubscriptionRequest)
            throws AmazonServiceException, AmazonClientException {
        return _client.confirmSubscription(confirmSubscriptionRequest);
    }

    @Override
    public GetTopicAttributesResult getTopicAttributes(GetTopicAttributesRequest getTopicAttributesRequest)
            throws AmazonServiceException, AmazonClientException {
        return _client.getTopicAttributes(getTopicAttributesRequest);
    }

    @Override
    public SubscribeResult subscribe(SubscribeRequest subscribeRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.subscribe(subscribeRequest);
    }

    @Override
    public void setTopicAttributes(SetTopicAttributesRequest setTopicAttributesRequest) throws AmazonServiceException,
            AmazonClientException {
        _client.setTopicAttributes(setTopicAttributesRequest);
    }

    @Override
    public void deleteTopic(DeleteTopicRequest deleteTopicRequest) throws AmazonServiceException, AmazonClientException {
        _client.deleteTopic(deleteTopicRequest);
    }

    @Override
    public void removePermission(RemovePermissionRequest removePermissionRequest) throws AmazonServiceException,
            AmazonClientException {
        _client.removePermission(removePermissionRequest);
    }

    @Override
    public ListSubscriptionsResult listSubscriptions(ListSubscriptionsRequest listSubscriptionsRequest)
            throws AmazonServiceException, AmazonClientException {
        return _client.listSubscriptions(listSubscriptionsRequest);
    }

    @Override
    public void addPermission(AddPermissionRequest addPermissionRequest) throws AmazonServiceException,
            AmazonClientException {
        _client.addPermission(addPermissionRequest);
    }

    @Override
    public CreateTopicResult createTopic(CreateTopicRequest createTopicRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.createTopic(createTopicRequest);
    }

    @Override
    public ListTopicsResult listTopics(ListTopicsRequest listTopicsRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.listTopics(listTopicsRequest);
    }

    @Override
    public void unsubscribe(UnsubscribeRequest unsubscribeRequest) throws AmazonServiceException, AmazonClientException {
        _client.unsubscribe(unsubscribeRequest);
    }

    @Override
    public ListSubscriptionsByTopicResult listSubscriptionsByTopic(
            ListSubscriptionsByTopicRequest listSubscriptionsByTopicRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.listSubscriptionsByTopic(listSubscriptionsByTopicRequest);
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

    @Override
    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return _client.getCachedResponseMetadata(request);
    }

}
