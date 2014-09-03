package org.skyscreamer.nevado.jms.connector.amazonaws;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.skyscreamer.nevado.jms.message.NevadoMessage;
import org.skyscreamer.nevado.jms.message.NevadoTextMessage;

/**
 * Overrides the serialisation handling from AbstractSQSConnector so that raw
 * text messages can be received/sent without wrapping.
 * 
 * @author qi.chen
 */
public class PlainTextAmazonSQSConnector extends AmazonAwsSQSConnector {

	public PlainTextAmazonSQSConnector(String awsAccessKey, String awsSecretKey, boolean isSecure,
			long receiveCheckIntervalMs) {
		super(awsAccessKey, awsSecretKey, isSecure, receiveCheckIntervalMs);
	}

	public PlainTextAmazonSQSConnector(String awsAccessKey, String awsSecretKey, boolean isSecure,
			long receiveCheckIntervalMs, boolean isAsync) {
		super(awsAccessKey, awsSecretKey, isSecure, receiveCheckIntervalMs, isAsync);
	}

	@Override
	protected String serializeMessage(NevadoMessage message) throws JMSException {
		if (message instanceof TextMessage) {
			return ((TextMessage) message).getText();
		} else {
			throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support " + message);
		}
	}

	@Override
	protected NevadoMessage deserializeMessage(String serializedMessage) throws JMSException {
		NevadoTextMessage out = new NevadoTextMessage();
		out.setText(serializedMessage);
		return out;
	}

}
