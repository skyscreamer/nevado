package org.skyscreamer.nevado.jms.connector.datastore;

import javax.annotation.Resource;

/**
 * Credentials for Google datastore-backed JMS implementation.
 *
 * User: carterp
 * Date: 11/4/13
 */
public class DatastoreCredentials {
    private String _datastoreServiceAccount;
    private Resource _datastorePrivateKeyFile;

    public DatastoreCredentials() {}

    public DatastoreCredentials(String datastoreServiceAccount, Resource datastorePrivateKeyFile) {
        _datastoreServiceAccount = datastoreServiceAccount;
        _datastorePrivateKeyFile = datastorePrivateKeyFile;
    }

    public String getDatastoreServiceAccount() {
        return _datastoreServiceAccount;
    }

    public void setDatastoreServiceAccount(String datastoreServiceAccount) {
        _datastoreServiceAccount = datastoreServiceAccount;
    }

    public Resource getDatastorePrivateKeyFile() {
        return _datastorePrivateKeyFile;
    }

    public void setDatastorePrivateKeyFile(Resource datastorePrivateKeyFile) {
        _datastorePrivateKeyFile = datastorePrivateKeyFile;
    }
}
