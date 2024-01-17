package com.camunda.academy;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.academy.handler.CreditCardServiceHandler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;

public class PaymentApplication {
	
	//Zeebe Client Credentials
    private static final String ZEEBE_ADDRESS = "[ZEEBE_ADDRESS]";
    private static final String ZEEBE_CLIENT_ID = "[ZEEBE_CLIENT_ID]";
    private static final String ZEEBE_CLIENT_SECRET = "[ZEEBE_CLIENT_SECRET]";
    private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "[ZEEBE_AUTHORIZATION_SERVER_URL]";
    private static final String ZEEBE_TOKEN_AUDIENCE = "[ZEEBE_TOKEN_AUDIENCE]";

	//Payment Application Details
	private static final int WORKER_TIMEOUT = 10;
	private static final int WORKER_TIME_TO_LIVE = 60;

	//Process Definition Details
	private static final String CREDIT_CARD_JOB_TYPE = "chargeCreditCard";

	//Logger
	private static Logger logger = LoggerFactory.getLogger(PaymentApplication.class);
	
	public static void main(String[] args){
    	
    	final OAuthCredentialsProvider credentialsProvider =
    			new OAuthCredentialsProviderBuilder()
			    	.authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
			        .audience(ZEEBE_TOKEN_AUDIENCE)
			        .clientId(ZEEBE_CLIENT_ID)
			        .clientSecret(ZEEBE_CLIENT_SECRET)
			        .build();
	    	
		try (final ZeebeClient client =
		        ZeebeClient.newClientBuilder()
		            .gatewayAddress(ZEEBE_ADDRESS)
		            .credentialsProvider(credentialsProvider)
		            .build()) {
			
			//Request the Cluster Topology
			logger.info("Zeebe Client Connected to: " + client.newTopologyRequest().send().join());
			
			//Start a Payment Worker
		    client.newWorker()
		        .jobType(CREDIT_CARD_JOB_TYPE)
		        .handler(new CreditCardServiceHandler())
		        .timeout(Duration.ofSeconds(WORKER_TIMEOUT).toMillis())
		        .open();
			logger.info("Payment Worker: Connected to Cluster");
			logger.info("Payment Worker: Processing Jobs for the next " + Duration.ofSeconds(WORKER_TIME_TO_LIVE).getSeconds() + " seconds");

			//Wait for the Workers
			Thread.sleep(Duration.ofSeconds(WORKER_TIME_TO_LIVE).toMillis());
			logger.info("Payment Worker: Disconnected from Cluster");
			
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }
}