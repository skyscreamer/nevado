package org.skyscreamer.nevado.jms.util;

import javax.jms.Message;

/**
 * Test MessageListener.  Throws exception on the first onMessage()
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TestMessageListenerRuntimeException extends TestMessageListener {
    private Message _firstMessage = null;

    public TestMessageListenerRuntimeException(boolean acknowledge) {
        super(acknowledge);
    }

    @Override
    public void onMessage(Message message) {
        if (_firstMessage == null)
        {
            _firstMessage = message;
            throw new RuntimeException("TESTING - Deliberately throwing RuntimeException");
        }
        super.onMessage(message);
    }

    public Message getFirstMessage() {
        return _firstMessage;
    }
}
