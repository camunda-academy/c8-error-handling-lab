package com.camunda.academy.service;

@SuppressWarnings("serial")
public class CreditCardServiceException extends Exception {
	
    public CreditCardServiceException(final String errorMessage){
        super(errorMessage);
    }
}