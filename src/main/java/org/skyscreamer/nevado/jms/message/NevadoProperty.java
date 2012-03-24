package org.skyscreamer.nevado.jms.message;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/24/12
 * Time: 2:42 PM
 */
public enum NevadoProperty {
    SQSReceiptHandle;

    public static final String PROVIDER_PREFIX = "JMS_nevado";

    @Override
    public String toString() {
        return PROVIDER_PREFIX + super.toString();
    }
}
