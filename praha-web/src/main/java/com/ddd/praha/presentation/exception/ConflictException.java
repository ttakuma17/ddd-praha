package com.ddd.praha.presentation.exception;

/**
 * リソースが競合状態にある場合の例外
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}