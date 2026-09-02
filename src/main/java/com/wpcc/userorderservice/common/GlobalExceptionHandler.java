package com.wpcc.userorderservice.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wpcc.userorderservice.common.exception.InsufficientStockException;
import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(
      MethodArgumentNotValidException exception) {

    Map<String, String> fieldErrors = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            fieldError -> Objects.requireNonNull(fieldError).getField(),
            fieldError -> Objects.requireNonNullElse(
                Objects.requireNonNull(fieldError).getDefaultMessage(),
                "请求参数不合法"),
            (first, ignored) -> first));

    log.warn("请求参数校验失败，fieldErrors={}", fieldErrors);

    ApiErrorResponse response = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "请求参数校验失败",
        fieldErrors);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException exception) {

    log.warn("非法参数：{}", exception.getMessage());

    ApiErrorResponse response = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        Objects.requireNonNullElse(exception.getMessage(), "请求参数不合法"),
        Map.of());

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
      ResourceNotFoundException exception) {
    log.warn("资源不存在：{}", exception.getMessage());

    ApiErrorResponse response = new ApiErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        exception.getMessage(),
        Map.of());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ApiErrorResponse> handleInsufficientStock(
      InsufficientStockException exception) {
    log.warn("库存不足：{}", exception.getMessage());

    ApiErrorResponse response = new ApiErrorResponse(
        HttpStatus.CONFLICT.value(),
        exception.getMessage(),
        Map.of());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }
}