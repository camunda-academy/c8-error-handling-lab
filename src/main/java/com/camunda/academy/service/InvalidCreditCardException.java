package com.camunda.academy.service;

@SuppressWarnings("serial")
public class InvalidCreditCardException extends Exception {
    
	public InvalidCreditCardException(String errorMessage) {
		super(errorMessage);
    }
}

