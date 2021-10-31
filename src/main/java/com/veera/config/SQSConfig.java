package com.veera.config;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SQSConfig {

	@Value("${AWS_ACCESS_KEY}")
	private String accessKey;

	@Value("${AWS_SECRET_KEY}")
	private String secretKey;
	
	@Value("${AWS_REGION}")
	private String region;

	@Bean
	public SqsClient sqsClient() {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
		return SqsClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
	}

	@Bean
	public SQSConnection sQSConnection() {
		// Create a new connection factory with all defaults (credentials and region)
		// set automatically
		SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),
				AmazonSQSClientBuilder.standard().withRegion(region)
						.withCredentials(
								new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
						.build());

		SQSConnection sQSConnection = null;
		// Create the connection.
		try {
			sQSConnection = connectionFactory.createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return sQSConnection;
	}

}
