package com.ddd.praha.presentation.exception;

/**
 * リクエストが不正な場合の例外
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}