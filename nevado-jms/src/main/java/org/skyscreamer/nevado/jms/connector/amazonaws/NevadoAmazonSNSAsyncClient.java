package org.skyscreamer.nevado.jms.connector.amazonaws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
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
import org.skyscreamer.nevado.jms.util.MessageIdUtil;

public class NevadoAmazonSNSAsyncClient implements AmazonSNS {

    private AmazonSNSAsyncClient _delegator;

    public NevadoAmazonSNSAsyncClient(AmazonSNSAsyncClient delegator) {
        _delegator = delegator;
    }
    
    @Override
    public void setEndpoint(String endpoint) throws IllegalArgumentException {
        _delegator.setEndpoint(endpoint);
    }
    
    @Override
    public void shutdown() {
        _delegator.shutdown();
    }

    @Override
    public ConfirmSubscriptionResult confirmSubscription(
            ConfirmSubscriptionRequest confirmSubscriptionRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.confirmSubscription(confirmSubscriptionRequest);
    }

    @Override
    public GetTopicAttributesResult getTopicAttributes(
            GetTopicAttributesRequest getTopicAttributesRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.getTopicAttributes(getTopicAttributesRequest);
    }

    @Override
    public SubscribeResult subscribe(SubscribeRequest subscribeRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.subscribe(subscribeRequest);
    }

    @Override
    public void setTopicAttributes(
            SetTopicAttributesRequest setTopicAttributesRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.setTopicAttributes(setTopicAttributesRequest);
    }

    @Override
    public void deleteTopic(DeleteTopicRequest deleteTopicRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.deleteTopic(deleteTopicRequest);
    }

    @Override
    public void removePermission(RemovePermissionRequest removePermissionRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.removePermission(removePermissionRequest);
    }

    @Override
    public ListSubscriptionsResult listSubscriptions(
            ListSubscriptionsRequest listSubscriptionsRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.listSubscriptions(listSubscriptionsRequest);
    }

    @Override
    public void addPermission(AddPermissionRequest addPermissionRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.addPermission(addPermissionRequest);
    }

    @Override
    public CreateTopicResult createTopic(CreateTopicRequest createTopicRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.createTopic(createTopicRequest);
    }

    @Override
    public ListTopicsResult listTopics(ListTopicsRequest listTopicsRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.listTopics(listTopicsRequest);
    }

    @Override
    public void unsubscribe(UnsubscribeRequest unsubscribeRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.unsubscribe(unsubscribeRequest);
    }

    @Override
    public ListSubscriptionsByTopicResult listSubscriptionsByTopic(
            ListSubscriptionsByTopicRequest listSubscriptionsByTopicRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator
                .listSubscriptionsByTopic(listSubscriptionsByTopicRequest);
    }

    @Override
    public PublishResult publish(PublishRequest publishRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.publishAsync(publishRequest);
        return new PublishResult().withMessageId(MessageIdUtil.createMessageId());
    }

    @Override
    public ListSubscriptionsResult listSubscriptions()
            throws AmazonServiceException, AmazonClientException {
        return _delegator.listSubscriptions();
    }

    @Override
    public ListTopicsResult listTopics() throws AmazonServiceException,
            AmazonClientException {
        return _delegator.listTopics();
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(
            AmazonWebServiceRequest request) {
        return _delegator.getCachedResponseMetadata(request);
    }

}
