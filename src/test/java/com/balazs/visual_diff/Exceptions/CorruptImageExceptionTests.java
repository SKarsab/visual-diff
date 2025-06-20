package com.balazs.visual_diff.Exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CorruptImageExceptionTests {
    private static final String DEFAULT_MESSAGE = "File is unsupported or corrupt.";

    @Test
    @DisplayName("Default constructor no param provided, sets default message")
    public void defaultConstructor_noParams_setsDefaultMessage() {
        //Act
        CorruptImageException exception = new CorruptImageException();

        //Assert
        assertEquals(DEFAULT_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with params provided, sets incoming message")
    public void defaultConstructor_withParams_setsIncomingMessage() {
        //Arrange
        String expectedMessage = "Expected Message";

        //Act
        CorruptImageException exception = new CorruptImageException(expectedMessage);

        //Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
}
