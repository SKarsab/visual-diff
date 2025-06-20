package com.balazs.visual_diff.Exceptions;

public class CorruptImageException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "File is unsupported or corrupt.";

    public CorruptImageException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public CorruptImageException(String message) {
        super(message);
    }
}
