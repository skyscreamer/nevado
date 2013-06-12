package org.skyscreamer.nevado.jms.connector.amazonaws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.AddPermissionRequest;
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
import com.amazonaws.services.sqs.model.RemovePermissionRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import org.skyscreamer.nevado.jms.util.MessageIdUtil;

public class NevadoAmazonSQSAsyncClient implements AmazonSQS {

    private AmazonSQSAsyncClient _delegator;
    
    public NevadoAmazonSQSAsyncClient(AmazonSQSAsyncClient delegator) {
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
    public ListQueuesResult listQueues(ListQueuesRequest listQueuesRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.listQueues(listQueuesRequest);
    }

    @Override
    public void setQueueAttributes(
            SetQueueAttributesRequest setQueueAttributesRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.setQueueAttributes(setQueueAttributesRequest);
    }

    @Override
    public void changeMessageVisibility(
            ChangeMessageVisibilityRequest changeMessageVisibilityRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.changeMessageVisibility(changeMessageVisibilityRequest);
    }

    @Override
    public CreateQueueResult createQueue(CreateQueueRequest createQueueRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.createQueue(createQueueRequest);
    }

    @Override
    public void removePermission(RemovePermissionRequest removePermissionRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.removePermission(removePermissionRequest);
    }

    @Override
    public GetQueueAttributesResult getQueueAttributes(
            GetQueueAttributesRequest getQueueAttributesRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.getQueueAttributes(getQueueAttributesRequest);
    }

    @Override
    public void addPermission(AddPermissionRequest addPermissionRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.addPermission(addPermissionRequest);
    }

    @Override
    public void deleteQueue(DeleteQueueRequest deleteQueueRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.deleteQueue(deleteQueueRequest);
    }

    @Override
    public void deleteMessage(DeleteMessageRequest deleteMessageRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.deleteMessage(deleteMessageRequest);
    }
    
    @Override
    public SendMessageResult sendMessage(SendMessageRequest sendMessageRequest)
            throws AmazonServiceException, AmazonClientException {
        _delegator.sendMessageAsync(sendMessageRequest);
        return new SendMessageResult().withMessageId(MessageIdUtil.createMessageId());
    }

    @Override
    public ReceiveMessageResult receiveMessage(
            ReceiveMessageRequest receiveMessageRequest)
            throws AmazonServiceException, AmazonClientException {
        return _delegator.receiveMessage(receiveMessageRequest);
    }

    @Override
    public ListQueuesResult listQueues() throws AmazonServiceException,
            AmazonClientException {
        return _delegator.listQueues();
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(
            AmazonWebServiceRequest request) {
        return _delegator.getCachedResponseMetadata(request);
    }

}
