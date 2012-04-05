package org.skyscreamer.nevado.jms.message;

import org.skyscreamer.nevado.jms.util.CharWrapper;
import org.skyscreamer.nevado.jms.util.PropertyConvertUtil;

import javax.jms.MessageFormatException;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 5:54 PM
 */
public class MapMessageConvertUtil extends PropertyConvertUtil {
    public static char convertToChar(String name, Object value) throws MessageFormatException {
        if (value instanceof CharWrapper) {
            return ((CharWrapper) value).charValue();
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "char"));
    }

    public static byte[] convertToBytes(String name, Object value) throws MessageFormatException {
        if (value instanceof ByteArray) {
            return ((ByteArray)value).toByteArray();
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "byte[]"));
    }

    public static String convertToString(String name, Object value) throws MessageFormatException {
        if (value instanceof String || value instanceof Boolean || value instanceof Byte || value instanceof Short
                || value instanceof Integer || value instanceof Long || value instanceof Float
                || value instanceof Double || value instanceof CharWrapper) {
            return value.toString();
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "string"));
    }

    public static void checkValidObject(Object value) throws MessageFormatException {
        if (!(value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer
                || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof String
                || value instanceof CharWrapper || value instanceof ByteArray || value == null)) {
            throw new MessageFormatException("Invalid value of type " + value.getClass().getName());
        }
    }
}
