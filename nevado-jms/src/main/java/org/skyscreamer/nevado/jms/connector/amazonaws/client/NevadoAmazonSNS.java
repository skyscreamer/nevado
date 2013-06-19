package org.skyscreamer.nevado.jms.connector.amazonaws.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
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

public interface NevadoAmazonSNS {

    public void setEndpoint(String endpoint) throws IllegalArgumentException;

    public void shutdown();

    public ConfirmSubscriptionResult confirmSubscription(ConfirmSubscriptionRequest confirmSubscriptionRequest)
            throws AmazonServiceException, AmazonClientException;

    public GetTopicAttributesResult getTopicAttributes(GetTopicAttributesRequest getTopicAttributesRequest)
            throws AmazonServiceException, AmazonClientException;

    public SubscribeResult subscribe(SubscribeRequest subscribeRequest) throws AmazonServiceException,
            AmazonClientException;

    public void setTopicAttributes(SetTopicAttributesRequest setTopicAttributesRequest) throws AmazonServiceException,
            AmazonClientException;

    public void deleteTopic(DeleteTopicRequest deleteTopicRequest) throws AmazonServiceException, AmazonClientException;

    public void removePermission(RemovePermissionRequest removePermissionRequest) throws AmazonServiceException,
            AmazonClientException;

    public ListSubscriptionsResult listSubscriptions(ListSubscriptionsRequest listSubscriptionsRequest)
            throws AmazonServiceException, AmazonClientException;

    public void addPermission(AddPermissionRequest addPermissionRequest) throws AmazonServiceException,
            AmazonClientException;

    public CreateTopicResult createTopic(CreateTopicRequest createTopicRequest) throws AmazonServiceException,
            AmazonClientException;

    public ListTopicsResult listTopics(ListTopicsRequest listTopicsRequest) throws AmazonServiceException,
            AmazonClientException;

    public void unsubscribe(UnsubscribeRequest unsubscribeRequest) throws AmazonServiceException, AmazonClientException;

    public ListSubscriptionsByTopicResult listSubscriptionsByTopic(
            ListSubscriptionsByTopicRequest listSubscriptionsByTopicRequest) throws AmazonServiceException,
            AmazonClientException;

    public PublishResult publish(PublishRequest publishRequest) throws AmazonServiceException, AmazonClientException;

    public ListSubscriptionsResult listSubscriptions() throws AmazonServiceException, AmazonClientException;

    public ListTopicsResult listTopics() throws AmazonServiceException, AmazonClientException;

    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request);

}
