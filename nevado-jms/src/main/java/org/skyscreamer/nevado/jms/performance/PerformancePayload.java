package org.skyscreamer.nevado.jms.performance;

import org.skyscreamer.nevado.jms.util.RandomData;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A payload for the performance service
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class PerformancePayload implements Serializable {
    private final int _id;
    private final byte[] _data;

    public PerformancePayload(int id, int payloadSize) {
        _id = id;
        _data = RandomData.readBytes(payloadSize);
    }

    public int getId() {
        return _id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PerformancePayload that = (PerformancePayload) o;

        if (_id != that._id) return false;
        if (!Arrays.equals(_data, that._data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id;
        result = 31 * result + (_data != null ? Arrays.hashCode(_data) : 0);
        return result;
    }
}
