package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.*;

/**
 * TODO - Add description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TopicSubscriberTest extends AbstractJMSTest {
    @Test
    public void testTopics() throws JMSException
    {
        NevadoSession session = createSession();
        TemporaryTopic testTopic = session.createTemporaryTopic();
        MessageProducer producer = session.createProducer(testTopic);
        MessageConsumer consumer1 = session.createConsumer(testTopic);
        MessageConsumer consumer2 = session.createConsumer(testTopic);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        producer.send(testMessage);;
        TextMessage msgOut1 = (TextMessage)consumer1.receive(1000);
        TextMessage msgOut2 = (TextMessage)consumer2.receive(1000);
        Assert.assertEquals(testMessage, msgOut1);
        Assert.assertEquals(testMessage, msgOut2);
    }
}
