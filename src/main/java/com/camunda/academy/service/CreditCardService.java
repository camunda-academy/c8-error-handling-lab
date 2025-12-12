package com.camunda.academy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.academy.PaymentApplication;

public class CreditCardService {

	//Credit Card Service
	private static final String CARD_NUMBER_ERROR = "error";
    private static final int CARD_EXPIRY_DATE_LENGTH = 7;
	
	//Logger
    private static Logger logger = LoggerFactory.getLogger(PaymentApplication.class);

	public String chargeCreditCard(final String reference,
                                   final String amount,
                                   final String cardNumber,
                                   final String cardExpiryDate,
                                   final String cardCVC)
                                		   throws CreditCardServiceException, InvalidCreditCardException {

    	//Output the Process Variables
		logger.info("Starting Transaction: " + reference);
		logger.info("Card Number: " + cardNumber);
		logger.info("Card Expiry Date: " + cardExpiryDate);
		logger.info("Card CVC: " + cardCVC);
		logger.info("Amount: " + amount);
		
		//Simulate an Exception in the Credit Card Service
	    if(cardNumber.equalsIgnoreCase(CARD_NUMBER_ERROR)){
	        throw new CreditCardServiceException("Credit Card Service: Internal Error");
	    }
		
	    //Simulate an Exception in the Credit Card Expiry Date
	    if(cardExpiryDate.length() != CARD_EXPIRY_DATE_LENGTH){
	    	throw new InvalidCreditCardException("Credit Card Service: Invalid Expiry Date");
	    }
		
		//Generate a Confirmation Number
		final String confirmation = String.valueOf(System.currentTimeMillis());
		logger.info("Successful Transaction: " + confirmation);
		return confirmation;
    }
}