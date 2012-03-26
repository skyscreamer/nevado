package org.skyscreamer.nevado.jms;

import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;
import com.xerox.amazonws.sqs2.SQSUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.message.*;
import org.skyscreamer.nevado.jms.util.SQSConnector;
import org.skyscreamer.nevado.jms.util.SerializeStringUtil;

import javax.jms.*;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: cpage
 * Date: 3/21/12
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class NevadoSession implements Session, QueueSession, TopicSession {
    private final SQSConnector _sqsConnector;
    private boolean _transacted;
    private int _acknowledgeMode;

    protected NevadoSession(String awsAccessKey, String awsSecretKey, boolean transacted, int acknowledgeMode) {
        _sqsConnector = new SQSConnector(awsAccessKey, awsSecretKey);
        _transacted = transacted;
        _acknowledgeMode = acknowledgeMode;
    }

    public BytesMessage createBytesMessage() throws JMSException {
        return null;  // TODO
    }

    public MapMessage createMapMessage() throws JMSException {
        NevadoMapMessage message = new NevadoMapMessage();
        message.setNevadoSession(this);
        return message;
    }

    public Message createMessage() throws JMSException {
        return null;  // TODO
    }

    public NevadoObjectMessage createObjectMessage() throws JMSException {
        NevadoObjectMessage message = new NevadoObjectMessage();
        message.setNevadoSession(this);
        return message;
    }

    public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException {
        NevadoObjectMessage message = createObjectMessage();
        message.setObject(serializable);
        return message;
    }

    public StreamMessage createStreamMessage() throws JMSException {
        NevadoStreamMessage message = new NevadoStreamMessage();
        message.setNevadoSession(this);
        return message;
    }

    public NevadoTextMessage createTextMessage() throws JMSException {
        NevadoTextMessage message = new NevadoTextMessage();
        message.setNevadoSession(this);
        return message;
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        NevadoTextMessage message = createTextMessage();
        message.setText(text);
        return message;
    }

    public boolean getTransacted() throws JMSException {
        return _transacted;
    }

    public int getAcknowledgeMode() throws JMSException {
        return _acknowledgeMode;
    }

    public void commit() throws JMSException {
        // TODO
    }

    public void rollback() throws JMSException {
        // TODO
    }

    public void close() throws JMSException {
        // TODO
    }

    public void recover() throws JMSException {
        // TODO
    }

    public MessageListener getMessageListener() throws JMSException {
        return null;  // TODO
    }

    public void setMessageListener(MessageListener messageListener) throws JMSException {
        // TODO
    }

    public void run() {
        // TODO
    }

    public MessageProducer createProducer(Destination destination) throws JMSException {
        return new NevadoMessageProducer(this, NevadoDestination.getInstance(destination));
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return new NevadoMessageConsumer(this, NevadoDestination.getInstance(destination));
    }

    public MessageConsumer createConsumer(Destination destination, String s) throws JMSException {
        return null;  // TODO
    }

    public MessageConsumer createConsumer(Destination destination, String s, boolean b) throws JMSException {
        return null;  // TODO
    }

    public Queue createQueue(String s) throws JMSException {
        return null;  // TODO
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        return null; // TODO
    }

    public QueueReceiver createReceiver(Queue queue, String s) throws JMSException {
        return null; // TODO
    }

    public QueueSender createSender(Queue queue) throws JMSException {
        return null; // TODO
    }

    public Topic createTopic(String s) throws JMSException {
        return null;  // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
        return null; // TODO
    }

    public TopicSubscriber createSubscriber(Topic topic, String s, boolean b) throws JMSException {
        return null; // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s) throws JMSException {
        return null;  // TODO
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b) throws JMSException {
        return null;  // TODO
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException {
        return null; // TODO
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return null;  // TODO
    }

    public QueueBrowser createBrowser(Queue queue, String s) throws JMSException {
        return null;  // TODO
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return null;  // TODO
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return null;  // TODO
    }

    public void unsubscribe(String s) throws JMSException {
        // TODO
    }

    public void sendMessage(NevadoDestination destination, NevadoMessage message) throws JMSException {
        message.onSend();
        _sqsConnector.sendMessage(destination, message);
    }

    public Message receiveMessage(NevadoDestination destination, long timeoutMs) throws JMSException {
        NevadoMessage message = _sqsConnector.receiveMessage(destination, timeoutMs);
        if (message != null) {
            message.setNevadoSession(this);
            message.setNevadoDestination(destination);
        }
        return message;
    }
    
    public void deleteMessage(NevadoMessage message) throws JMSException {
        _sqsConnector.deleteMessage(message);
    }
}
