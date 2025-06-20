package com.balazs.visual_diff.Exceptions;

public class InvalidTypeException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Invalid 'type' value in body. Must be 'baseline' or 'comparison'.";

    public InvalidTypeException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public InvalidTypeException(String message) {
        super(message);
    }
}
