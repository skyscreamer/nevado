package org.skyscreamer.nevado.jms.connector.mock;

/**
 * Any bean than implements this interface must provide an implementation which
 * returns the bean back to a pristine empty state.
 */
public interface ResettableMock {

    void reset();

}
