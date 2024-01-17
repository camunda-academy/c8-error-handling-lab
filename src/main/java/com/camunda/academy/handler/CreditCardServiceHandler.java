package com.camunda.academy.handler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.camunda.academy.service.CreditCardService;
import com.camunda.academy.service.CreditCardServiceException;
import com.camunda.academy.service.InvalidCreditCardException;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

public class CreditCardServiceHandler implements JobHandler {
	
	//BPMN Errors
    private static final String BPMN_ERROR_INVALID_CARD_EXPIRY_DATE = "cardExpiryDateError";
	
	//Process Variables
	private static final String VARIABLE_CARD_CVC = "cardCVC";
	private static final String VARIABLE_CARD_EXPIRY = "cardExpiry";
	private static final String VARIABLE_CARD_NUMBER = "cardNumber";
	private static final String VARIABLE_AMOUNT = "amount";
	private static final String VARIABLE_REFERENCE = "reference";
	private static final String VARIABLE_CONFIRMATION = "confirmation";
	
	//Create a Credit Card Service for Testing
    CreditCardService creditCardService = new CreditCardService();

    @Override
    public void handle(JobClient client, ActivatedJob job) throws CreditCardServiceException, InvalidCreditCardException {

    	//Obtain the Process Variables
    	final Map<String, Object> inputVariables = job.getVariablesAsMap();
    	final String reference = (String) inputVariables.get(VARIABLE_REFERENCE);
    	final String amount = (String) inputVariables.get(VARIABLE_AMOUNT);
    	final String cardNumber = (String) inputVariables.get(VARIABLE_CARD_NUMBER);
    	final String cardExpiry = (String) inputVariables.get(VARIABLE_CARD_EXPIRY);
    	final String cardCVC =  (String) inputVariables.get(VARIABLE_CARD_CVC);

    	try {

    			//Charge the Credit Card
    			final String confirmation = creditCardService.chargeCreditCard(reference, amount, cardNumber, cardExpiry, cardCVC);

    			//Build the Output Process Variables
    			final Map<String, Object> outputVariables = new HashMap<String, Object>();
    			outputVariables.put(VARIABLE_CONFIRMATION, confirmation);

    			//Complete the Job
    			client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();

    		} catch (CreditCardServiceException e) {
    	        int retries = job.getRetries();
    	        client.newFailCommand(job.getKey())
    	                .retries(retries-1)
    	                .retryBackoff(Duration.ofSeconds(20))
    	                .errorMessage(e.getMessage())
    	                .send()
    	                .join();
    		} catch (InvalidCreditCardException e) {
    		    client.newThrowErrorCommand(job.getKey())
    		    	    .errorCode(BPMN_ERROR_INVALID_CARD_EXPIRY_DATE)
    		    	    .send()
    		    	    .join();
    		}
    }
}