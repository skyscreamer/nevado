package org.skyscreamer.nevado.jms.util;

import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;

import javax.jms.MessageFormatException;

/**
 * Created by IntelliJ IDEA.
 * User: cpage
 * Date: 3/21/12
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertUtil {
    public static boolean convertToBoolean(String name, Object value) throws MessageFormatException {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (value instanceof String) {
            return Boolean.valueOf((String) value);
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "boolean"));
    }

    public static byte convertToByte(String name, Object value) throws MessageFormatException {
        if (value instanceof Byte) {
            return (Byte)value;
        }
        if (value instanceof String) {
            return Byte.valueOf((String)value);
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "byte"));
    }

    public static short convertToShort(String name, Object value) throws MessageFormatException {
        if (value instanceof Short) {
            return (Short)value;
        }
        if (value instanceof Byte) {
            return Short.valueOf((Short)value);
        }
        if (value instanceof String) {
            return Short.valueOf((String)value);
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "short"));
    }

    public static int convertToInt(String name, Object value) throws MessageFormatException {
        if (value instanceof Integer) {
            return (Integer)value;
        }
        if (value instanceof Short || value instanceof Byte) {
            return ((Number)value).intValue();
        }
        if (value instanceof String) {
            return Integer.valueOf((String)value);
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "integer"));
    }

    public static long convertToLong(String name, Object value) throws MessageFormatException {
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return ((Number)value).longValue();
        }
        if (value instanceof String) {
            return Long.valueOf((String)value);
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "long"));
    }

    public static float convertToFloat(String name, Object value) throws MessageFormatException {
        if (value instanceof Float) {
            return (Float)value;
        }
        if (value instanceof String) {
            return Float.valueOf((String)value);
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "float"));
    }

    public static double convertToDouble(String name, Object value) throws MessageFormatException {
        if (value instanceof Double) {
            return (Double)value;
        }
        if (value instanceof Float) {
            return Double.valueOf((Float)value);
        }
        if (value instanceof String) {
            return Double.valueOf((String)value);
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "double"));
    }

    public static String convertToString(String name, Object value) throws MessageFormatException {
        if (value instanceof String || value instanceof Boolean || value instanceof Byte || value instanceof Short
            || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
            return value.toString();
        }
        throw new MessageFormatException(createExceptionMessage(name, value, "string"));
    }

    private static String createExceptionMessage(String name, Object value, String destType) {
        return "Cannot convert " + name + " from a " + value.getClass().getName() + " to a " + destType + ".";
    }
}
