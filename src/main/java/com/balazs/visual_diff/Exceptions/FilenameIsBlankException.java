package com.balazs.visual_diff.Exceptions;

public class FilenameIsBlankException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Filename is empty or blank.";

    public FilenameIsBlankException() 
    { 
        super(DEFAULT_MESSAGE);
    }

    public FilenameIsBlankException(String message) {
        super(message);
    }
}
