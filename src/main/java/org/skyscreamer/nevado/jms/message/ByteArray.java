package org.skyscreamer.nevado.jms.message;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 5:11 PM
 */
class ByteArray implements Serializable {
    private final byte[] _value;

    private transient int hash = 0; // Cache

    public ByteArray() {
        _value = new byte[0];
    }

    public ByteArray(byte[] value) {
        _value = Arrays.copyOf(value, value.length);
    }

    public ByteArray(byte[] value, int offset, int length) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException("Index of of range: " + offset);
        }
        if (length < 0) {
            throw new IndexOutOfBoundsException("Index of of range: " + length);
        }
        if (offset + length > value.length) {
            throw new IndexOutOfBoundsException("Index of of range: " + (offset + length));
        }
        _value = Arrays.copyOfRange(value, offset, offset + length);
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(_value, _value.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteArray byteArray = (ByteArray) o;
        if (!Arrays.equals(_value, byteArray._value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = Arrays.hashCode(_value);
        }
        return hash;
    }
}
