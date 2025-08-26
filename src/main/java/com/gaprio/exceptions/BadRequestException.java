package com.gaprio.exceptions;

/**
 * Thrown when request is invalid or forbidden.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

