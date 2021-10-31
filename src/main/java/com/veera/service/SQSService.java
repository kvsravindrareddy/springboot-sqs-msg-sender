package com.veera.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.veera.data.Employee;
import com.veera.util.Utils;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
public class SQSService {
	
	@Autowired
	private SqsClient sqsClient;
	
	@Value("${AWS_SQS_QUEUE_URL}")
	private String sqsQueueUrl;
	
	@Value("${AWS_SQS_OBJECT_QUEUE_URL}")
	private String sqsObjectQueueUrl;
	

	public String sendMessage(String msg)
	{
		String response = null;
		try {
		sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(sqsQueueUrl).messageBody(msg)
				.delaySeconds(10).build());
		response = "Success";
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return response;
	}
	
	public String sendMessageObject(Employee emp)
	{
		String response = null;
		String encodedEmpObj = null;
		try {
			encodedEmpObj = Utils.serializeToBase64(emp);
		sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(sqsObjectQueueUrl).messageBody(encodedEmpObj)
				.delaySeconds(10).build());
		response = "Success";
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return response;
	}
	
	
	public List<String> receiveMessage()
	{
		List<String> resultMsgs = null;
		try {
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(sqsQueueUrl)
					.maxNumberOfMessages(5).build();
			List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
			if(!messages.isEmpty() && null!=messages)
			{
				resultMsgs = messages.stream().map(msgs->msgs.body()).collect(Collectors.toList());
			}
		} catch (SqsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return resultMsgs;
	}
}
