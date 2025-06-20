package com.balazs.visual_diff.Exceptions;

public class NotFoundException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "File is not found.";

    public NotFoundException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
