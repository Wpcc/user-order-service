package com.wpcc.userorderservice.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    ApiErrorResponse response = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "请求参数校验失败",
        fieldErrors);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException exception) {
    ApiErrorResponse response = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        Objects.requireNonNullElse(exception.getMessage(), "请求参数不合法"),
        Map.of());

    return ResponseEntity.badRequest().body(response);
  }
}