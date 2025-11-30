package com.example.gitapi.exception;

/**
 * Exception thrown when a Git operation fails.
 */
public class GitOperationException extends RuntimeException {

    public GitOperationException(String message) {
        super(message);
    }

    public GitOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
