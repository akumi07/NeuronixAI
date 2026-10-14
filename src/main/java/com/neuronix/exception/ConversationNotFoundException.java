package com.neuronix.exception;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(String message) {
//added comment to reflect changes
        super(message);

    }

}
