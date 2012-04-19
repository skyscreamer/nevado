package org.skyscreamer.nevado.jms;

import javax.jms.*;

/**
 * Nevado implementation of TopicConnection
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoTopicConnection extends NevadoConnection implements TopicConnection {
    public NevadoTopicConnection(String awsAccessKey, String awsSecretKey) throws JMSException {
        super(awsAccessKey, awsSecretKey);
    }

    @Override
    public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        _inUse = true;
        NevadoTopicSession nevadoSession = new NevadoTopicSession(this, transacted, acknowledgeMode);
        initializeSession(nevadoSession);
        return nevadoSession;
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Topic topic, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        checkClosed();
        _inUse = true;
        return null;  // TODO
    }
}
