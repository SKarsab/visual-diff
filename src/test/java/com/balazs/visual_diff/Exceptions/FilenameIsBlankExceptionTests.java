package com.balazs.visual_diff.Exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FilenameIsBlankExceptionTests {
    private static final String DEFAULT_MESSAGE = "Filename is empty or blank.";

    @Test
    @DisplayName("Default constructor no param provided, sets default message")
    public void defaultConstructor_noParams_setsDefaultMessage() {
        //Act
        FilenameIsBlankException exception = new FilenameIsBlankException();

        //Assert
        assertEquals(DEFAULT_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with params provided, sets incoming message")
    public void defaultConstructor_withParams_setsIncomingMessage() {
        //Arrange
        String expectedMessage = "Expected Message";

        //Act
        FilenameIsBlankException exception = new FilenameIsBlankException(expectedMessage);

        //Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
}
