package com.wpcc.userorderservice.jdbc;

public class JdbcDataAccessException extends RuntimeException {
  public JdbcDataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
