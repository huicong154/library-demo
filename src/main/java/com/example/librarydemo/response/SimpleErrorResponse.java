package com.example.librarydemo.response;

public class SimpleErrorResponse {
    private String message;
    private int errorCode;

    public SimpleErrorResponse(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
