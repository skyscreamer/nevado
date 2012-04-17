package org.skyscreamer.nevado.jms.connector;

import com.xerox.amazonws.sqs2.*;
import com.xerox.amazonws.sqs2.Message;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoMessageConsumer;
import org.skyscreamer.nevado.jms.NevadoMessageProducer;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.connector.NevadoConnector;
import org.skyscreamer.nevado.jms.connector.SQSConnector;
import org.skyscreamer.nevado.jms.destination.NevadoQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTemporaryQueue;
import org.skyscreamer.nevado.jms.destination.NevadoTopic;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

/**
 * TODO - Add description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TopicSubscriberTest extends AbstractJMSTest {
//    @Test
    public void testTopics() throws JMSException, InterruptedException {
        NevadoSession session = createSession();
        NevadoTopic testTopic = new NevadoTopic("testTopic");
        NevadoMessageProducer producer = session.createProducer(testTopic);
        NevadoMessageConsumer consumer1 = session.createConsumer(testTopic);
        NevadoMessageConsumer consumer2 = session.createConsumer(testTopic);
        SQSConnector sqsConnector = (SQSConnector)getConnection().getSQSConnector();
        String topicARN = sqsConnector.getTopicARN(testTopic);
        String testData = RandomData.readString();
        sqsConnector.sendSNSMessage(topicARN, testData);


        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        Thread.sleep(5000);
        producer.send(testMessage);;
        TextMessage msgOut1 = (TextMessage)consumer1.receive(1000);
        TextMessage msgOut2 = (TextMessage)consumer2.receive(1000);
        Assert.assertEquals(testMessage, msgOut1);
        Assert.assertEquals(testMessage, msgOut2);
    }

    @Test
    public void testRawPubSub() throws JMSException, InterruptedException, SQSException, JSONException {
        NevadoSession session = createSession();
        NevadoTopic testTopic = new NevadoTopic("testTopic");
        NevadoQueue topicEndpoint = session.createTemporaryQueue();
        String subscriptionArn = session.subscribe((NevadoTopic)testTopic, topicEndpoint);

        SQSConnector sqsConnector = (SQSConnector)getConnection().getSQSConnector();
        String topicARN = sqsConnector.getTopicARN(testTopic);
        String testData = RandomData.readString();
        sqsConnector.sendSNSMessage(topicARN, testData);
        MessageQueue sqsQueue = sqsConnector.getSQSQueue(topicEndpoint);
        sqsQueue.setEncoding(false);
        Thread.sleep(1000);
        Message sqsMessage = sqsQueue.receiveMessage();
        JSONObject messsageJSON = new JSONObject(sqsMessage.getMessageBody());
        String message = messsageJSON.getString("Message");
        Assert.assertEquals(testData, message);
    }
}
