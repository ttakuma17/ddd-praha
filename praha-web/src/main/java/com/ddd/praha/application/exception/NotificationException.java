package com.ddd.praha.application.exception;

public class NotificationException extends RuntimeException {
  public NotificationException(String message, Throwable cause) {
    super(message, cause);
  }
}
