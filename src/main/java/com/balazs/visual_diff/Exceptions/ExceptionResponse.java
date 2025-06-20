package com.balazs.visual_diff.Exceptions;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;

public class ExceptionResponse {
    
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ExceptionResponse() { }

    public ExceptionResponse(HttpStatus newStatus, String newMessage, String newPath) {
        timestamp = OffsetDateTime.now().toString();
        status = newStatus.value();
        error = newStatus.getReasonPhrase();
        message = newMessage;
        path = newPath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String newTimeString) {
        timestamp = newTimeString;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int newStatus) {
        status = newStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String newError) {
        error = newError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String newPath) {
        path = newPath;
    }
}
