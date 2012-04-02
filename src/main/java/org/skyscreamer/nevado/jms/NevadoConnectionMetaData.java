package org.skyscreamer.nevado.jms;

import org.skyscreamer.nevado.jms.message.JMSXProperty;

import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 4/2/12
 * Time: 12:15 AM
 */
public class NevadoConnectionMetaData implements ConnectionMetaData {
    private static final NevadoConnectionMetaData INSTANCE = new NevadoConnectionMetaData();

    public static ConnectionMetaData getInstance() {
        return INSTANCE;
    }

    public String getJMSVersion() throws JMSException {
        return "1.1";
    }

    public int getJMSMajorVersion() throws JMSException {
        return 1;
    }

    public int getJMSMinorVersion() throws JMSException {
        return 1;
    }

    public String getJMSProviderName() throws JMSException {
        return "nevado";
    }

    public String getProviderVersion() throws JMSException {
        return "0.1";
    }

    public int getProviderMajorVersion() throws JMSException {
        return 0;
    }

    public int getProviderMinorVersion() throws JMSException {
        return 1;
    }

    public Enumeration getJMSXPropertyNames() throws JMSException {
        List<JMSXProperty> propertyList = JMSXProperty.getSupportedProperties();
        Vector<String> propertyNames = new Vector<String>(propertyList.size());
        for(JMSXProperty property : propertyList) {
            propertyNames.add(property.name());
        }
        return propertyNames.elements();
    }
}
