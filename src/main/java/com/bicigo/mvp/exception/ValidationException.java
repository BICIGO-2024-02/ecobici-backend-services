package com.bicigo.mvp.exception;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }
}
