package org.skyscreamer.nevado.jms.message;

import org.activemq.message.ActiveMQObjectMessage;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.util.RandomData;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.io.Serializable;
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
        ObjectMessage msg = createSession().createObjectMessage();
        testObjectMessage(msg);
    }

    @Test
    public void testAlienObjectMessage() throws JMSException {
        ObjectMessage msg = new ActiveMQObjectMessage();
        testObjectMessage(msg);
    }

    private void testObjectMessage(ObjectMessage msg) throws JMSException {
        clearTestQueue();
        TestObject testObject = new TestObject();
        msg.setObject(testObject);
        Message msgOut = sendAndReceive(msg);
        Assert.assertTrue("Should be an object message", msgOut instanceof ObjectMessage);
        Assert.assertEquals("Object should be equal", testObject, ((ObjectMessage)msgOut).getObject());
    }

    @Test
    public void testObjectMessage2() throws JMSException {
        clearTestQueue();

        TestObject testObject = new TestObject();
        ObjectMessage msg = createSession().createObjectMessage(testObject);
        Message msgOut = sendAndReceive(msg);
        Assert.assertTrue("Should be an object message", msgOut instanceof ObjectMessage);
        Assert.assertEquals("Object should be equal", testObject, ((ObjectMessage)msgOut).getObject());
    }

    @Test
    public void testBadObjectMessage() throws JMSException {
        clearTestQueue();

        TestObject testObject = new TestObject();
        ObjectMessage msg = createSession().createObjectMessage();
        msg.setObject(testObject);
        Message msgOut = sendAndReceive(msg);
        Assert.assertTrue("Should be an object message", msgOut instanceof ObjectMessage);
        // To be paranoid, let's check pulling something out and see that it fails
        ((Map)testObject._map.get("c")).remove("d");
        Assert.assertFalse("Object should be equal", testObject.equals(((ObjectMessage)msgOut).getObject()));
    }

    private static class TestObject implements Serializable {
        private final String _string = UUID.randomUUID().toString();
        private final int _int = (new Random()).nextInt();
        private final Map<String, Object> _map = new HashMap<String, Object>();
        private final List<Object> _list = new ArrayList<Object>();

        private TestObject() {
            _map.put("a", RandomData.readString());
            _map.put("b", RandomData.readString());
            Map<String, Object> subMap = new HashMap<String, Object>();
            subMap.put("d", RandomData.readString());
            subMap.put("e", RandomData.readString());
            _map.put("c", subMap);
            
            _list.add(RandomData.readString());
            _list.add(RandomData.readInt());
            List<Object> subList = new ArrayList<Object>();
            subList.add(RandomData.readInt());
            subList.add(RandomData.readString());
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
