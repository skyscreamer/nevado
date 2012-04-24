package org.skyscreamer.nevado.jms.destination;

import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoSession;

import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.MessageProducer;

/**
 * Test InvalidDestination
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class InvalidDestinationTest extends AbstractJMSTest {
    @Test(expected = InvalidDestinationException.class)
    public void testDeletedDestination() throws JMSException {
        NevadoSession session = createSession();
        NevadoTemporaryQueue temporaryQueue = session.createTemporaryQueue();
        MessageProducer producer = session.createProducer(temporaryQueue);
        getConnection().deleteTemporaryQueue(temporaryQueue);
        producer.send(session.createMessage());
    }
}
