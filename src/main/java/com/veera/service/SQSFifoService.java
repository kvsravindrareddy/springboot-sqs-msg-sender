package com.veera.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.veera.data.Employee;
import com.veera.util.Utils;

@Service
public class SQSFifoService {
	
	@Value("${FIFO_QUEUE}")
	private String inputFifoQueue;
	
	@Value("${FIFO_EMP_QUEUE}")
	private String inputFifoEmpQueue;
	
	@Autowired
	public SQSConnection sQSConnection;
	
	private void createQueue()
	{
		// Get the wrapped client
		AmazonSQSMessagingClientWrapper client = sQSConnection.getWrappedAmazonSQSClient();

		// Create an Amazon SQS FIFO queue named MyQueue.fifo, if it doesn't already exist
		try {
			if (!client.queueExists(inputFifoQueue)) {
			    Map<String, String> attributes = new HashMap<String, String>();
			    attributes.put("FifoQueue", "true");
			    attributes.put("ContentBasedDeduplication", "true");
			    client.createQueue(new CreateQueueRequest().withQueueName(inputFifoQueue).withAttributes(attributes));
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToFifoQueue(String msg) throws JMSException
	{
		createQueue();
		// Create the nontransacted session with AUTO_ACKNOWLEDGE mode
		Session session = sQSConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Create a queue identity and specify the queue name to the session
		Queue queue = session.createQueue(inputFifoQueue);
		 
		// Create a producer for the 'MyQueue'
		MessageProducer producer = session.createProducer(queue);
		// Create the text message
		TextMessage message = session.createTextMessage(msg);

		// Set the message group ID
		message.setStringProperty("JMSXGroupID", "Default");

		// You can also set a custom message deduplication ID
		// message.setStringProperty("JMS_SQS_DeduplicationId", "hello");
		// Here, it's not needed because content-based deduplication is enabled for the queue

		// Send the message
		producer.send(message);
		System.out.println("JMS Message " + message.getJMSMessageID());
		System.out.println("JMS Message Sequence Number " + message.getStringProperty("JMS_SQS_SequenceNumber"));
	}

	public void sendMessageObject(Employee emp) throws JMSException, IOException {
		createQueue();
		// Create the nontransacted session with AUTO_ACKNOWLEDGE mode
		Session session = sQSConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Create a queue identity and specify the queue name to the session
		Queue queue = session.createQueue(inputFifoEmpQueue);
		 
		// Create a producer for the 'MyQueue'
		MessageProducer producer = session.createProducer(queue);
		String encodedEmpObj = Utils.serializeToBase64(emp);
		// Create the text message
		TextMessage message = session.createTextMessage(encodedEmpObj);

		// Set the message group ID
		message.setStringProperty("JMSXGroupID", "Default");

		// You can also set a custom message deduplication ID
		message.setStringProperty("JMS_SQS_DeduplicationId", String.valueOf(UUID.randomUUID()));
		// Here, it's not needed because content-based deduplication is enabled for the queue

		// Send the message
		producer.send(message);
		System.out.println("JMS Message " + message.getJMSMessageID());
		System.out.println("JMS Message Sequence Number " + message.getStringProperty("JMS_SQS_SequenceNumber"));
	}

}