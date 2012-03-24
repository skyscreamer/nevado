package org.skyscreamer.nevado.jms;

import org.activemq.message.ActiveMQQueue;
import org.activemq.message.ActiveMQTopic;

import javax.jms.*;
import java.util.Queue;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/24/12
 * Time: 10:22 AM
 */
public abstract class NevadoDestination implements Destination {
    private final String _name;

    public NevadoDestination(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public static NevadoDestination getInstance(Destination destination) {
        NevadoDestination nevadoDestination = null;

        if (destination != null) {
            if (destination instanceof NevadoDestination) {
                nevadoDestination = (NevadoDestination) destination;
            }
            else {
                if (destination instanceof TemporaryQueue) {
                    // Create new NevadoTemporaryQueue - TODO
                }
                else if (destination instanceof TemporaryTopic) {
                    // Create new NevadoTemporaryTopic - TODO
                }
                else if (destination instanceof Queue) {
                    // Create new NevadoQueue - TODO
                }
                else if (destination instanceof Topic) {
                    // Create new NevadoTopic - TODO
                }
            }
        }

        return nevadoDestination;
    }
    
    public String toString() {
        return _name;
    }
}
