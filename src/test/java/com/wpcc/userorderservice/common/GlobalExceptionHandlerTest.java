package com.wpcc.userorderservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wpcc.userorderservice.common.exception.InsufficientStockException;
import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
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

  @Test
  void returnsNotFoundForMissingResource() {
    ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResourceNotFound(
        new ResourceNotFoundException("商品不存在：10"));

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(404, response.getBody().status());
    assertEquals("商品不存在：10", response.getBody().message());
  }

  @Test
  void returnsConflictForInsufficientStock() {
    ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleInsufficientStock(
        new InsufficientStockException("库存不足：10"));

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(409, response.getBody().status());
    assertEquals("库存不足：10", response.getBody().message());
  }
}
