package com.balazs.visual_diff.Exceptions;

public class EmptyFileException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Uploaded file is empty/missing.";

    public EmptyFileException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public EmptyFileException(String message) {
        super(message);
    }
}
