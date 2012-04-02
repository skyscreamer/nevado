package org.skyscreamer.nevado.jms.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 4/1/12
 * Time: 11:37 PM
 */
public enum JMSXProperty {
    JMSXUserID(String.class, false),
    JMSXAppID(String.class, false),
    JMSXDeliveryCount(Integer.class, false),
    JMSXGroupID(String.class, true),
    JMSXGroupSeq(Integer.class, true),
    JMSXProducerTXID(String.class, false),
    JMSXConsumerTXID(String.class, false),
    JMSXRcvTimestamp(Long.class, false),
    JMSXState(Integer.class, false);

    private static List<JMSXProperty> SUPPORTED_PROPERTIES = null;
    private final Class _type;
    private final boolean _supported;

    private JMSXProperty(Class type, boolean supported) {
        _type = type;
        _supported = supported;
    }

    public static synchronized List<JMSXProperty> getSupportedProperties() {
        if (SUPPORTED_PROPERTIES == null) {
            initializeProperties();
        }
        return SUPPORTED_PROPERTIES;
    }

    private static void initializeProperties() {
        SUPPORTED_PROPERTIES = new ArrayList<JMSXProperty>();
        for(JMSXProperty property : values()) {
            if (property.isSupported()) {
                SUPPORTED_PROPERTIES.add(property);
            }
        }
    }

    public Class getType() {
        return _type;
    }

    public boolean isSupported() {
        return _supported;
    }
}
