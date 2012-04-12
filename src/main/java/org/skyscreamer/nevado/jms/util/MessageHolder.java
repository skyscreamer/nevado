package org.skyscreamer.nevado.jms.util;

import org.skyscreamer.nevado.jms.NevadoSession;
import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.Destination;
import javax.jms.JMSException;
import java.util.*;

/**
 * MessageHolder to allow sessions to hold onto messages in CLIENT_ACKNOWLEDGE mode
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageHolder {
    private final NevadoSession _session;
    private final Map<Destination, List<NevadoMessage>> _messageHolder
            = new HashMap<Destination, List<NevadoMessage>>();
    private final Map<Destination, Integer> _messageIndex = new HashMap<Destination, Integer>();

    public MessageHolder(NevadoSession session) {
        _session = session;
    }

    public void add(Destination destination, NevadoMessage msg)
    {
        if (!_messageHolder.containsKey(destination))
        {
            _messageHolder.put(destination, new ArrayList<NevadoMessage>());
        }

        List<NevadoMessage> messageList = _messageHolder.get(destination);
        int index = _messageIndex.containsKey(destination) ? _messageIndex.get(destination) : 0;
        if (index < messageList.size())
        {
            throw new IllegalStateException("Cannot add message while in replay mode");
        }
        messageList.add(msg);
        _messageIndex.put(destination, index + 1);
    }

    public NevadoMessage getNextMessage(Destination destination) {
        NevadoMessage message = null;
        if (_messageHolder.containsKey(destination))
        {
            List<NevadoMessage> messageList = _messageHolder.get(destination);
            int index = _messageIndex.get(destination);

            if (index < messageList.size())
            {
                message = messageList.get(index);
                _messageIndex.put(destination, index + 1);
            }
        }
        return message;
    }

    public void acknowledgeConsumedMessages() throws JMSException
    {
        // Separate the wheat from the chaff
        List<NevadoMessage> consumedMessages = new ArrayList<NevadoMessage>();
        List<NevadoMessage> unconsumedMessages = new ArrayList<NevadoMessage>();
        for(Destination destination : _messageHolder.keySet())
        {
            for(NevadoMessage msg : getConsumedMessages(destination))
            {
                consumedMessages.add(msg);
            }
            for(NevadoMessage msg : getUnconsumedMessages(destination))
            {
                unconsumedMessages.add(msg);
            }
        }

        // Delete consumed messages, reset the rest
        _session.deleteMessage(consumedMessages.toArray(new NevadoMessage[0]));
        for(NevadoMessage msg : consumedMessages)
        {
            msg.setAcknowledged(true);
        }
        _session.resetMessage(unconsumedMessages.toArray(new NevadoMessage[0]));

        // Re-initialize state
        _messageHolder.clear();
        _messageIndex.clear();
    }

    private List<NevadoMessage> getConsumedMessages(Destination destination)
    {
        if (_messageHolder.containsKey(destination))
        {
            List<NevadoMessage> messageList = _messageHolder.get(destination);
            int index = _messageIndex.get(destination);
            return messageList.subList(0, index);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private List<NevadoMessage> getUnconsumedMessages(Destination destination)
    {
        if (_messageHolder.containsKey(destination))
        {
            List<NevadoMessage> messageList = _messageHolder.get(destination);
            int index = _messageIndex.get(destination);
            return messageList.subList(index, messageList.size());
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public void reset()
    {
        for(Destination destination : _messageIndex.keySet())
        {
            _messageIndex.put(destination, 0);
        }
    }
}
