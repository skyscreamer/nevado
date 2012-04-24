package org.skyscreamer.nevado.jms.facilities;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.nevado.jms.AbstractJMSTest;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;

import javax.jms.*;

/**
 * Tests for Client ID (JMS 1.1, Sec. 4.3.2)
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class ClientIDTest extends AbstractJMSTest {
    private static final String TEST_CLIENT_ID = "testClientID";

    @Test
    public void testClientID1() throws JMSException {
        Connection conn = createConnection(getConnectionFactory());
        Assert.assertNull(conn.getClientID());
        conn.setClientID(TEST_CLIENT_ID);
        Assert.assertEquals(TEST_CLIENT_ID, conn.getClientID());
    }

    @Test(expected = javax.jms.IllegalStateException.class)
    public void testClientID2() throws JMSException {
        Connection conn = createConnection(getConnectionFactory());
        Assert.assertNull(conn.getClientID());
        conn.setClientID(TEST_CLIENT_ID);
        conn.setClientID("somethingelse");
    }

    @Test
    public void testClientID3() throws JMSException {
        NevadoConnectionFactory connectionFactory = new NevadoConnectionFactory();
        connectionFactory.setClientID(TEST_CLIENT_ID);
        Connection conn = createConnection(connectionFactory);
        Assert.assertEquals(TEST_CLIENT_ID, conn.getClientID());
    }

    @Test
    public void testClientID4() throws JMSException {
        Connection conn1 = createConnection(getConnectionFactory());
        Connection conn2 = createConnection(getConnectionFactory());
        conn1.setClientID(TEST_CLIENT_ID);
        conn1.close();
        conn2.setClientID(TEST_CLIENT_ID);
    }

    @Test(expected = javax.jms.IllegalStateException.class)
    public void testClientID5() throws JMSException {
        Connection conn = createConnection(getConnectionFactory());
        Assert.assertNull(conn.getClientID());
        conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        conn.setClientID(TEST_CLIENT_ID);
    }

    @Test(expected = InvalidClientIDException.class)
    public void testBadClientID() throws JMSException {
        Connection conn = createConnection(getConnectionFactory());
        conn.setClientID("[here's a bad id]");
    }
}
