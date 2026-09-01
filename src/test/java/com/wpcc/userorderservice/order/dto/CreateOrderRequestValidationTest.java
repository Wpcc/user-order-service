package com.wpcc.userorderservice.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class CreateOrderRequestValidationTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void rejectsMissingIdsAndInvalidQuantity() {
    Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(
        new CreateOrderRequest(null, null, 0));

    assertEquals(3, violations.size());
    assertTrue(violations.stream()
        .anyMatch(violation -> "用户 ID 不能为空".equals(violation.getMessage())));
    assertTrue(violations.stream()
        .anyMatch(violation -> "商品 ID 不能为空".equals(violation.getMessage())));
    assertTrue(violations.stream()
        .anyMatch(violation -> "购买数量必须大于等于 1".equals(violation.getMessage())));
  }
}
