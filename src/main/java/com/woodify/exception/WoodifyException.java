package com.woodify.exception;

public class WoodifyException extends RuntimeException {
    public WoodifyException(String message) {
        super(message);
    }

    public WoodifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
