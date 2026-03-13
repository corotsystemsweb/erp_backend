package com.sms.exception;
public class FeeAssignmentConflictException extends RuntimeException {
    public FeeAssignmentConflictException(String message) {
        super(message);
    }

    public FeeAssignmentConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
