package com.balazs.visual_diff.Exceptions;

public class DimensionMismatchException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Baseline and comparison files do not have matching dimensions.";

    public DimensionMismatchException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public DimensionMismatchException(String message) {
        super(message);
    }
}
