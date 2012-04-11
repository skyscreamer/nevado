package org.skyscreamer.nevado.jms.util;

import org.skyscreamer.nevado.jms.message.NevadoMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * MessageHolder to allow sessions to hold onto messages in CLIENT_ACKNOWLEDGE mode
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class MessageHolder {
    private final List<NevadoMessage> _messageList = new ArrayList<NevadoMessage>();
    private int _index = 0;

    public void clear()
    {
        _messageList.clear();
        _index = 0;
    }

    public int size()
    {
        return _messageList.size();
    }

    public void add(NevadoMessage msg)
    {
        if (_index < _messageList.size())
        {
            throw new IllegalStateException("Cannot add message while in replay mode");
        }
        _messageList.add(msg);
        ++_index;
    }

    public void reset()
    {
        _index = 0;
    }

    public NevadoMessage getNextMessage() {
        NevadoMessage message = null;
        if (_index < _messageList.size())
        {
            message = _messageList.get(_index);
            ++_index;
        }
        return message;
    }

    public List<NevadoMessage> getMessageList() {
        return _messageList;
    }
}
