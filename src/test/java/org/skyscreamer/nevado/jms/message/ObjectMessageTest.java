package org.skyscreamer.nevado.jms.message;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 11:26 AM
 */
public class ObjectMessageTest extends AbstractJMSTest {
    @Test
    public void testObjectMessage() throws JMSException {
        TestObject testObject = new TestObject();
        ObjectMessage msg = getSession().createObjectMessage();
        msg.setObject(testObject);
        getSession().createProducer(getTestQueue()).send(msg);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertTrue("Should be an object message", msgOut instanceof ObjectMessage);
        Assert.assertEquals("Object should be equal", testObject, ((ObjectMessage)msgOut).getObject());
    }

    @Test
    public void testBadObjectMessage() throws JMSException {
        TestObject testObject = new TestObject();
        ObjectMessage msg = getSession().createObjectMessage();
        msg.setObject(testObject);
        getSession().createProducer(getTestQueue()).send(msg);
        Message msgOut = getSession().createConsumer(getTestQueue()).receive();
        Assert.assertNotNull("Got null message back", msgOut);
        msgOut.acknowledge();
        Assert.assertTrue("Should be an object message", msgOut instanceof ObjectMessage);
        // To be paranoid, let's check pulling something out and see that it fails
        ((Map)testObject._map.get("c")).remove("d");
        Assert.assertFalse("Object should be equal", testObject.equals(((ObjectMessage)msgOut).getObject()));
    }

    private static class TestObject implements Serializable {
        private final String _string = UUID.randomUUID().toString();
        private final int _int = (new Random()).nextInt();
        private final HashMap _map = new HashMap();
        private final List _list = new ArrayList();

        private TestObject() {
            _map.put("a", UUID.randomUUID().toString());
            _map.put("b", UUID.randomUUID().toString());
            Map subMap = new HashMap();
            subMap.put("d", UUID.randomUUID().toString());
            subMap.put("e", UUID.randomUUID().toString());
            _map.put("c", subMap);
            
            _list.add(UUID.randomUUID().toString());
            _list.add(UUID.randomUUID().toString());
            List subList = new ArrayList();
            subList.add(UUID.randomUUID().toString());
            subList.add(UUID.randomUUID().toString());
            _list.add(subList);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestObject that = (TestObject) o;

            if (_int != that._int) return false;
            if (!_list.equals(that._list)) return false;
            if (!_map.equals(that._map)) return false;
            if (_string != null ? !_string.equals(that._string) : that._string != null) return false;

            return true;
        }
    }
}
