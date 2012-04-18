package org.skyscreamer.nevado.jms.destination;

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
    @Test
    public void testTopics() throws JMSException, InterruptedException {
        NevadoSession session = createSession();
        NevadoTopic testTopic = new NevadoTopic("testTopic");
        NevadoMessageProducer producer = session.createProducer(testTopic);
        NevadoMessageConsumer consumer1 = session.createConsumer(testTopic);
        NevadoMessageConsumer consumer2 = session.createConsumer(testTopic);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        producer.send(testMessage);
        TextMessage msgOut1 = (TextMessage)consumer1.receive(1000);
        TextMessage msgOut2 = (TextMessage)consumer2.receive(1000);
        Assert.assertEquals(testMessage.getText(), msgOut1.getText());
        Assert.assertEquals(testMessage.getText(), msgOut2.getText());
    }
}
