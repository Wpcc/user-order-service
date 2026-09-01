package com.wpcc.userorderservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @Test
  void returnsUnifiedResponseForIllegalArgumentException() {
    ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleIllegalArgumentException(
        new IllegalArgumentException("库存不足：10"));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(400, response.getBody().status());
    assertEquals("库存不足：10", response.getBody().message());
    assertTrue(response.getBody().fieldErrors().isEmpty());
  }
}
