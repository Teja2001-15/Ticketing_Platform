package com.antiscalping.tickets.exceptions;

public class FraudDetectedException extends RuntimeException {
    public FraudDetectedException(String message) {
        super(message);
    }
    
    public FraudDetectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
