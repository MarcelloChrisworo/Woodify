package com.woodify.exception;

public class DatabaseConnectionException extends WoodifyException {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
