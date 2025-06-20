package com.balazs.visual_diff.Exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

public class GlobalExceptionHandlerTests {

    @Test
    @DisplayName("handleNoHandlerException triggered, returns 404")
    public void handleNoHandlerException_validInput_shouldReturn404NotFound() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "Expected Message";

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        NoHandlerFoundException exception = mock(NoHandlerFoundException.class);

        when(exception.getMessage()).thenReturn(expectedMessage);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleNoHandlerException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleMethodArgumentNotValidException triggered, returns 400")
    public void handleMethodArgumentNotValidException_validInput_shouldReturn400BadRequest() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Method argument validation failed. Must not be null or blank.";

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        when(exception.getMessage()).thenReturn(expectedMessage);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleMethodArgumentNotValidException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCorruptImageException triggered, returns 400")
    public void handleCorruptImageException_validInput_shouldReturn400BadRequest() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "File is unsupported or corrupt."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        CorruptImageException exception = mock(CorruptImageException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleCorruptImageException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleDimensionMismatchException triggered, returns 400")
    public void handleDimensionMismatchException_validInput_shouldReturn400BadRequest() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Baseline and comparison files do not have matching dimensions."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        DimensionMismatchException exception = mock(DimensionMismatchException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleDimensionMismatchException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleEmptyFileException triggered, returns 400")
    public void handleEmptyFileException_validInput_shouldReturn400BadRequest() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Uploaded file is empty/missing."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        EmptyFileException exception = mock(EmptyFileException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleEmptyFileException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleInvalidTypeException triggered, returns 400")
    public void handleInvalidTypeException_validInput_shouldReturn400BadRequest() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Invalid 'type' value in body. Must be 'baseline' or 'comparison'."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        InvalidTypeException exception = mock(InvalidTypeException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleInvalidTypeException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleNotFoundException triggered, returns 404")
    public void handleNotFoundException_validInput_shouldReturn404NotFound() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "File is not found."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        NotFoundException exception = mock(NotFoundException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleNotFoundException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleTooLargeFileException triggered, returns 413")
    public void handleTooLargeFileException_validInput_shouldReturn413TooLarge() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.PAYLOAD_TOO_LARGE;
        String expectedMessage = "File size exceeds the maximum allowed limit (5MB)."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        MaxUploadSizeExceededException exception = mock(MaxUploadSizeExceededException.class);

        when(exception.getMessage()).thenReturn(expectedMessage);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleTooLargeFileException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleUnsupportedMediaTypeException triggered, returns 415")
    public void handleUnsupportedMediaTypeException_validInput_shouldReturn415UnsupportedMedia() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        String expectedMessage = "File is not PNG or JPEG format."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        UnsupportedMediaTypeException exception = mock(UnsupportedMediaTypeException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleUnsupportedMediaTypeException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleUnsupportedMediaTypeException triggered, returns 400")
    public void handleUnsupportedMediaTypeException_validInput_shouldReturn400BadRequest() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "Filename is empty or blank."; //const from class

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        FilenameIsBlankException exception = mock(FilenameIsBlankException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleUnsupportedMediaTypeException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("handleIOException triggered, returns 400")
    public void handleIOException_validInput_shouldReturn400BadRequest() {
        //Arrange
        HttpStatus expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String expectedMessage = "Internal server error. Please try again later.";

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        IOException exception = mock(IOException.class);

        //Act
        ResponseEntity<ExceptionResponse> actualResponse = globalExceptionHandler.handleIOException(exception, request);

        //Assert
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }
}
