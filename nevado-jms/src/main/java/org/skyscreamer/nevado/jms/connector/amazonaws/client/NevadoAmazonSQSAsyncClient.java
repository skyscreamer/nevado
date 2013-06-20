package org.skyscreamer.nevado.jms.connector.amazonaws.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import org.skyscreamer.nevado.jms.util.MessageIdUtil;

import java.util.concurrent.ExecutorService;

public class NevadoAmazonSQSAsyncClient implements NevadoAmazonSQS {

    private AmazonSQSAsync _client;

    public NevadoAmazonSQSAsyncClient(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration,
            ExecutorService executorService) {
        _client = new AmazonSQSAsyncClient(awsCredentials, clientConfiguration, executorService);
    }

    @Override
    public void setEndpoint(String endpoint) throws IllegalArgumentException {
        _client.setEndpoint(endpoint);
    }

    @Override
    public ListQueuesResult listQueues(ListQueuesRequest listQueuesRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.listQueues(listQueuesRequest);
    }

    @Override
    public void setQueueAttributes(SetQueueAttributesRequest setQueueAttributesRequest) throws AmazonServiceException,
            AmazonClientException {
        _client.setQueueAttributes(setQueueAttributesRequest);
    }

    @Override
    public void changeMessageVisibility(ChangeMessageVisibilityRequest changeMessageVisibilityRequest)
            throws AmazonServiceException, AmazonClientException {
        _client.changeMessageVisibility(changeMessageVisibilityRequest);
    }

    @Override
    public CreateQueueResult createQueue(CreateQueueRequest createQueueRequest) throws AmazonServiceException,
            AmazonClientException {
        return _client.createQueue(createQueueRequest);
    }

    @Override
    public GetQueueAttributesResult getQueueAttributes(GetQueueAttributesRequest getQueueAttributesRequest)
            throws AmazonServiceException, AmazonClientException {
        return _client.getQueueAttributes(getQueueAttributesRequest);
    }

    @Override
    public void deleteQueue(DeleteQueueRequest deleteQueueRequest) throws AmazonServiceException, AmazonClientException {
        _client.deleteQueue(deleteQueueRequest);
    }

    @Override
    public void deleteMessage(DeleteMessageRequest deleteMessageRequest) throws AmazonServiceException,
            AmazonClientException {
        _client.deleteMessage(deleteMessageRequest);
    }

    @Override
    public SendMessageResult sendMessage(SendMessageRequest sendMessageRequest) throws AmazonServiceException,
            AmazonClientException {
        _client.sendMessageAsync(sendMessageRequest);
        return new SendMessageResult().withMessageId(MessageIdUtil.createMessageId());
    }

    @Override
    public ReceiveMessageResult receiveMessage(ReceiveMessageRequest receiveMessageRequest)
            throws AmazonServiceException, AmazonClientException {
        return _client.receiveMessage(receiveMessageRequest);
    }

    @Override
    public ListQueuesResult listQueues() throws AmazonServiceException, AmazonClientException {
        return _client.listQueues();
    }

}
