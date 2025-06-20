package com.balazs.visual_diff.Exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TooLargeFileExceptionTests {
    private static final String DEFAULT_MESSAGE = "File size exceeds the maximum allowed limit (5MB).";

    @Test
    @DisplayName("Default constructor no param provided, sets default message")
    public void defaultConstructor_noParams_setsDefaultMessage() {
        //Act
        TooLargeFileException exception = new TooLargeFileException();

        //Assert
        assertEquals(DEFAULT_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with params provided, sets incoming message")
    public void defaultConstructor_withParams_setsIncomingMessage() {
        //Arrange
        String expectedMessage = "Expected Message";

        //Act
        TooLargeFileException exception = new TooLargeFileException(expectedMessage);

        //Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
}
