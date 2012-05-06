package org.skyscreamer.nevado.jms.destination;

/**
 * Nevado provider queue prefixes.  Use this enum so we can keep these reserved.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public enum NevadoProviderQueuePrefix {
    TEMPORARY_DESTINATION_PREFIX("nevado_temp_"), DURABLE_SUBSCRIPTION_PREFIX("nevado_durable_topic_");

    private final String _value;

    private NevadoProviderQueuePrefix(String value)
    {
        _value = value;
    }

    public static boolean isValidQueueName(String queueName) {
        if (queueName == null || "".equals(queueName.trim()))
        {
            return false;
        }
        for(NevadoProviderQueuePrefix prefix : values())
        {
            if (queueName.startsWith(prefix._value))
            {
                return false;
            }
        }
        return true;
    }

    public String getValue() {
        return _value;
    }

    @Override
    public String toString() {
        return _value;
    }
}
