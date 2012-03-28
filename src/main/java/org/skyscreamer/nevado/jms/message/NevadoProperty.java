package org.skyscreamer.nevado.jms.message;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/24/12
 * Time: 2:42 PM
 */
public enum NevadoProperty {
    SQSReceiptHandle(String.class), DisableMessageID(Boolean.class);

    public static final String PROVIDER_PREFIX = "JMS_nevado";
    private final Class _propertyType;
    private NevadoProperty(Class propertyType) {
        _propertyType = propertyType;
    }

    public Class getPropertyType() {
        return _propertyType;
    }

    @Override
    public String toString() {
        return PROVIDER_PREFIX + super.toString();
    }
}
