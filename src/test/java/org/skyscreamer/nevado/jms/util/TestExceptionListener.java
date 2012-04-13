package org.skyscreamer.nevado.jms.util;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TODO - Add description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class TestExceptionListener implements ExceptionListener {
    private List<JMSException> _exceptions = new CopyOnWriteArrayList<JMSException>();

    public void onException(JMSException e) {
        _exceptions.add(e);
    }

    public List<JMSException> getExceptions() {
        return _exceptions;
    }
}
