package org.skyscreamer.nevado.jms.destination;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.*;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

/**
 * Test basic topic calls.
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
        producer.close();
        consumer1.close();
        consumer2.close();
        ((NevadoConnection)getConnection()).deleteTopic(testTopic);
    }

    @Test
    public void testNoLocal() throws JMSException
    {
        getConnection().close(); // Don't use the provided connection
        NevadoConnection conn1 = createConnectionFactory().createConnection();
        conn1.start();
        NevadoConnection conn2 = createConnectionFactory().createConnection();
        conn2.start();
        NevadoSession session1 = conn1.createSession(false, Session.AUTO_ACKNOWLEDGE);
        NevadoSession session2 = conn2.createSession(false, Session.AUTO_ACKNOWLEDGE);
        NevadoTopic testTopic = new NevadoTopic("testTopic");
        NevadoMessageProducer producer1 = session1.createProducer(testTopic);
        NevadoMessageProducer producer2 = session2.createProducer(testTopic);
        NevadoMessageConsumer consumer1 = session1.createConsumer(testTopic, null, true);
        NevadoMessageConsumer consumer2 = session2.createConsumer(testTopic, null, true);
        NevadoMessageConsumer consumer3 = session2.createConsumer(testTopic, null, false);

        TextMessage testMessage1 = session1.createTextMessage(RandomData.readString());
        producer1.send(testMessage1);
        TextMessage testMessage2 = session2.createTextMessage(RandomData.readString());
        producer2.send(testMessage2);

        Assert.assertEquals(testMessage2, (TextMessage)consumer1.receive(1000));
        Assert.assertNull((TextMessage)consumer1.receive(200));
        Assert.assertEquals(testMessage1, (TextMessage)consumer2.receive(1000));
        Assert.assertNull((TextMessage)consumer2.receive(200));
        TextMessage msgOut1 = (TextMessage)consumer3.receive(1000);
        TextMessage msgOut2 = (TextMessage)consumer3.receive(1000);
        compareTextMessages(new TextMessage[] {testMessage1, testMessage2}, new TextMessage[] {msgOut1, msgOut2});
        conn1.close();
        conn2.close();
    }
}
