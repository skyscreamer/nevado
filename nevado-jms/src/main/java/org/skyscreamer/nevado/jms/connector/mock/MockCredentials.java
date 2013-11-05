package org.skyscreamer.nevado.jms.connector.mock;

import org.skyscreamer.nevado.jms.connector.CloudCredentials;

/**
 * Mock credentials.
 *
 * User: carterp
 * Date: 11/4/13
 */
public class MockCredentials implements CloudCredentials {
    private String _testData;

    public MockCredentials() {}

    public MockCredentials(String testData) {
        _testData = testData;
    }

    public String getTestData() {
        return _testData;
    }

    public void setTestData(String testData) {
        _testData = testData;
    }
}
