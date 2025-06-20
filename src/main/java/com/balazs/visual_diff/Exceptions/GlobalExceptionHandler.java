package com.balazs.visual_diff.Exceptions;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Global exception handler for CorruptImageException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when the incoming Multipartfile cannot be converted to a BufferedImage after valdiating it is not empty, and 
     * correct content type.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(CorruptImageException.class)
    public ResponseEntity<ExceptionResponse> handleCorruptImageException(CorruptImageException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, new CorruptImageException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
        logger.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for DimensionMismatchException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when the desired baseline and comparison images have different dimensions.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(DimensionMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleDimensionMismatchException(DimensionMismatchException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, new DimensionMismatchException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
        logger.error("DimensionMismatchException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for EmptyFileException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when the incoming Multipart file is .isEmpty().
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ExceptionResponse> handleEmptyFileException(EmptyFileException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, new EmptyFileException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
        logger.error("EmptyFileException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for InvalidTypeException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when the incomign type does not match 'baseline' or 'comparison'.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(InvalidTypeException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTypeException(InvalidTypeException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, new InvalidTypeException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
        logger.error("InvalidTypeException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for NotFoundException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when the file or resource is not found in Azure Blob Storage.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND, new NotFoundException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
        logger.error("NotFoundException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for MaxUploadSizeExceededException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when the incoming request is over the maximum (5MB/10MB) in application.properties. This is intercepted before it reaches 
     * the endpoint.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleTooLargeFileException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.PAYLOAD_TOO_LARGE, new TooLargeFileException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(exceptionResponse);
        logger.error("MaxUploadSizeExceededException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for UnsupportedMediaTypeException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when incoming image does not match image/png or image/jpeg.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ExceptionResponse> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, new UnsupportedMediaTypeException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(exceptionResponse);
        logger.error("UnsupportedMediaTypeException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for FilenameIsBlankException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when incoming fileName is null, or just whitespace.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(FilenameIsBlankException.class)
    public ResponseEntity<ExceptionResponse> handleUnsupportedMediaTypeException(FilenameIsBlankException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, new FilenameIsBlankException().getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
        logger.error("FilenameIsBlankException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for IOException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionResponse> handleIOException(Exception e, HttpServletRequest request) {
        String errorMessage = "Internal server error. Please try again later.";
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
        logger.error("IOException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for MethodArgumentNotValidException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when validation on any parameters annoteated by @Valid does not pass.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = "Method argument validation failed. Must not be null or blank.";
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST, errorMessage, request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
        logger.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
        return response;
    }

    /**
     * Global exception handler for NoHandlerFoundException. Used to catch, log, and return response of the error including: timestamp, status, error, 
     * message, path. This happens when a path cannot be evaluated to an endpoint.
     *
     * @param Exception e
     * @param HttpServletRequest request used to getRequestURI
     * @return ResponseEntity<ExceptionResponse> response that mimics Spring Boot's default contents, timestamp, status, error, message, path
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoHandlerException(NoHandlerFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
        ResponseEntity<ExceptionResponse> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
        logger.error("NoHandlerFoundException: {}", e.getMessage(), e);
        return response;
    }
}
