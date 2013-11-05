package org.skyscreamer.nevado.jms.connector.datastore;

import org.skyscreamer.nevado.jms.connector.AbstractSQSConnectorFactory;
import org.skyscreamer.nevado.jms.connector.CloudCredentials;
import org.skyscreamer.nevado.jms.connector.SQSConnector;

import javax.jms.JMSException;

/**
 * Google doesn't have a queue service in its cloud offering, but it does have some nice infrastructure that can be
 * leveraged to program SQS/SNS functionality on top of it.
 *
 * In this case we'll use the Google Datastore NoSQL system to store our queues and topic metadata.  Queue is the
 * primitive form we'll use, and we'll add some semantics to allow broadcasting to multiple queues based on metadata,
 * which will simulate the pub/sub aspect.
 *
 * Each queue will be represented by a table in the system.  That will generate hotspots and eventually the client
 * or this connector will need to support sharding across M tables, with readers doing a round-robin through them.  That
 * would break absolute FIFO guarantees (which don't exist on SQS either), but would allow much better scaling.
 *
 * User: carter@skyscreamer.org
 * Date: 11/4/13
 * Time: 4:57 PM
 */
public class DatastoreConnectorFactory extends AbstractSQSConnectorFactory {
    private String datasetId;

    @Override
    public SQSConnector getInstance(CloudCredentials credentials) throws JMSException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
