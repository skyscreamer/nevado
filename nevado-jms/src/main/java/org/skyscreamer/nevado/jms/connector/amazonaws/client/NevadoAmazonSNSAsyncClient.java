package org.skyscreamer.nevado.jms.connector.amazonaws.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
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
import org.skyscreamer.nevado.jms.util.MessageIdUtil;

import java.util.concurrent.ExecutorService;

public class NevadoAmazonSNSAsyncClient implements NevadoAmazonSNS {

    private AmazonSNSAsync _client;

    public NevadoAmazonSNSAsyncClient(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration,
            ExecutorService executorService) {
        _client = new AmazonSNSAsyncClient(awsCredentials, clientConfiguration, executorService);
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
        _client.publishAsync(publishRequest);
        return new PublishResult().withMessageId(MessageIdUtil.createMessageId());
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
