package com.balazs.visual_diff.Exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ExceptionResponseTests {
    @Test
    @DisplayName("Default constructor, initializes empty object")
    public void ExceptionResponse_default_shouldInitializeEmptyObject() {
        //Act
        ExceptionResponse actualResponse = new ExceptionResponse();

        //Assert
        assertEquals(0, actualResponse.getStatus());
        assertNull(actualResponse.getTimestamp());
        assertNull(actualResponse.getError());
        assertNull(actualResponse.getMessage());
        assertNull(actualResponse.getPath());
    }

    @Test
    @DisplayName("Parameterized constructor, initializes object correctly")
    public void ExceptionResponse_parameters_shouldInitializeObject() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Bad Request";
        String expectedPath = "/files/upload";

        //Act
        ExceptionResponse actualResponse = new ExceptionResponse(expectedStatus, expectedMessage, expectedPath);

        //Assert
        assertEquals(expectedStatus.value(), actualResponse.getStatus());
        assertEquals(expectedMessage, actualResponse.getMessage());
        assertEquals(expectedPath, actualResponse.getPath());
    }

    @Test
    @DisplayName("Get and set timestamp")
    public void changeTimestamp_withValue_changesTimeStamp() {
        //Arrange
        ExceptionResponse actualResponse = new ExceptionResponse();
        String expectedTime = "2025-06-05_22-14-22";

        //Act
        actualResponse.setTimestamp(expectedTime);
        String actualTime = actualResponse.getTimestamp();

        //Assert
        assertEquals(expectedTime, actualTime);
    }

    @Test
    @DisplayName("Get and set status")
    public void changeStatus_withValue_changesStatus() {
        //Arrange
        ExceptionResponse actualResponse = new ExceptionResponse();
        int expectedStatus = HttpStatus.BAD_REQUEST.value();

        //Act
        actualResponse.setStatus(expectedStatus);
        int actualStatus = actualResponse.getStatus();

        //Assert
        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Get and set message")
    public void changeMessage_withValue_changesMessage() {
        //Arrange
        ExceptionResponse actualResponse = new ExceptionResponse();
        String expectedMessage = "Bad Request";

        //Act
        actualResponse.setMessage(expectedMessage);
        String actualMessage = actualResponse.getMessage();

        //Assert
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    @DisplayName("Get and set path")
    public void changePath_withValue_changesPaths() {
        //Arrange
        ExceptionResponse actualResponse = new ExceptionResponse();
        String expectedPath = "/files/upload";

        //Act
        actualResponse.setPath(expectedPath);
        String actualPath = actualResponse.getPath();

        //Assert
        assertEquals(expectedPath, actualPath);
    }
}
