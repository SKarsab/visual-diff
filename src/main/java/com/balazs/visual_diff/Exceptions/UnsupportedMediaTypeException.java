package com.balazs.visual_diff.Exceptions;

public class UnsupportedMediaTypeException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "File is not PNG or JPEG format.";

    public UnsupportedMediaTypeException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public UnsupportedMediaTypeException(String message) {
        super(message);
    }
}
