package org.skyscreamer.nevado.jms.connector.amazonaws.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
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

public interface NevadoAmazonSNS {

    public void setEndpoint(String endpoint) throws IllegalArgumentException;

    public SubscribeResult subscribe(SubscribeRequest subscribeRequest) throws AmazonServiceException,
            AmazonClientException;

    public void deleteTopic(DeleteTopicRequest deleteTopicRequest) throws AmazonServiceException, AmazonClientException;

    public CreateTopicResult createTopic(CreateTopicRequest createTopicRequest) throws AmazonServiceException,
            AmazonClientException;

    public void unsubscribe(UnsubscribeRequest unsubscribeRequest) throws AmazonServiceException, AmazonClientException;

    public PublishResult publish(PublishRequest publishRequest) throws AmazonServiceException, AmazonClientException;

    public ListSubscriptionsResult listSubscriptions() throws AmazonServiceException, AmazonClientException;

    public ListTopicsResult listTopics() throws AmazonServiceException, AmazonClientException;

}
