package com.balazs.visual_diff.Exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InvalidTypeExceptionTests {
    private static final String DEFAULT_MESSAGE = "Invalid 'type' value in body. Must be 'baseline' or 'comparison'.";

    @Test
    @DisplayName("Default constructor no param provided, sets default message")
    public void defaultConstructor_noParams_setsDefaultMessage() {
        //Act
        InvalidTypeException exception = new InvalidTypeException();

        //Assert
        assertEquals(DEFAULT_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with params provided, sets incoming message")
    public void defaultConstructor_withParams_setsIncomingMessage() {
        //Arrange
        String expectedMessage = "Expected Message";

        //Act
        InvalidTypeException exception = new InvalidTypeException(expectedMessage);

        //Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
}
