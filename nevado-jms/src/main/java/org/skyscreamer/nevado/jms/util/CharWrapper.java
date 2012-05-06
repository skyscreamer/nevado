package org.skyscreamer.nevado.jms.util;

import java.io.Serializable;

/**
 * A simple wrapper to get around the fact that Hessian doesn't properly convert java.lang.Character.
 *
 * Details on the Hessian problem here:
 *
 * http://stackoverflow.com/questions/10011696/hessian-deserializes-java-lang-character-as-a-string
 */
public class CharWrapper implements Serializable {
    private final char _c;

    public CharWrapper(char c) {
        _c = c;
    }

    public CharWrapper(Character c) {
        _c =c;
    }

    public char charValue() {
        return _c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharWrapper that = (CharWrapper) o;

        if (_c != that._c) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) _c;
    }

    @Override
    public String toString() {
        return _c + "";
    }
}
