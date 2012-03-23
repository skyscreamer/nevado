package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.message.NevadoTextMessage;

import javax.jms.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: cpage
 * Date: 3/21/12
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class NevadoSession implements Session, QueueSession, TopicSession {
    private String _awsAccessKey;
    private String _awsSecretKey;
    private boolean _transacted;
    private int _acknowledgeMode;

    protected NevadoSession(String awsAccessKey, String awsSecretKey, boolean transacted, int acknowledgeMode) {
        _awsAccessKey = awsAccessKey;
        _awsSecretKey = awsSecretKey;
        _transacted = transacted;
        _acknowledgeMode = acknowledgeMode;
    }

    public BytesMessage createBytesMessage() throws JMSException {
        return null;  // TODO
    }

    public MapMessage createMapMessage() throws JMSException {
        return null;  // TODO
    }

    public Message createMessage() throws JMSException {
        return null;  // TODO
    }

    public ObjectMessage createObjectMessage() throws JMSException {
        return null;  // TODO
    }

    public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException {
        return null;  // TODO
    }

    public StreamMessage createStreamMessage() throws JMSException {
        return null;  // TODO
    }

    public TextMessage createTextMessage() throws JMSException {
        return new NevadoTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        TextMessage message = new NevadoTextMessage();
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
        return new NevadoMessageProducer(destination);
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return new NevadoMessageConsumer(destination);
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
}
