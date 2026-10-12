package com.neuronix.exception;



public class MessageNotFoundException extends RuntimeException {
//added message to reflect changes
    public MessageNotFoundException(String message) {
        super(message);
    }
}