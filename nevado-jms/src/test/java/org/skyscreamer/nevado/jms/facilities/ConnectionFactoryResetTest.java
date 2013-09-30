package org.skyscreamer.nevado.jms.facilities;

import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.skyscreamer.nevado.jms.connector.mock.ResettableMock;
import org.skyscreamer.nevado.jms.util.RandomData;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.util.Collection;

public class ConnectionFactoryResetTest extends AbstractJMSTest{

    private static final String QUEUE_NAME = "QUEUE_NAME";

    @Autowired
    private Collection<ResettableMock> _resettableMocks;

    @Test
    public void testResetWillEmptyQueue() throws Exception {

        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory(_sqsConnectorFactory);
        Connection connection = createConnection(connectionFactory);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue(QUEUE_NAME);
        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage testMessage = session.createTextMessage(RandomData.readString());
        producer.send(testMessage);

        for (ResettableMock resettableMock : _resettableMocks) {
            resettableMock.reset();
        }

        Message msgOut = consumer.receive(2000);
        Assert.assertNull(msgOut);
        connection.close();

    }
}