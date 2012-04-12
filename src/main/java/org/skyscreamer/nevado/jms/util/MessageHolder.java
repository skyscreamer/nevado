package org.skyscreamer.nevado.jms.util;

import org.skyscreamer.nevado.jms.message.NevadoMessage;

import javax.jms.Destination;
import java.util.*;

/**
 * MessageHolder to allow sessions to hold onto messages in CLIENT_ACKNOWLEDGE mode
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageHolder {
    private final Map<Destination, List<NevadoMessage>> _messageHolder
            = new HashMap<Destination, List<NevadoMessage>>();
    private final Map<Destination, Integer> _messageIndex = new HashMap<Destination, Integer>();

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

    public List<NevadoMessage> getConsumedMessages(Destination destination)
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

    public List<NevadoMessage> getUnconsumedMessages(Destination destination)
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
