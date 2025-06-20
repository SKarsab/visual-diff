package com.balazs.visual_diff.Exceptions;

public class TooLargeFileException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "File size exceeds the maximum allowed limit (5MB).";

    public TooLargeFileException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public TooLargeFileException(String message) {
        super(message);
    }
}
