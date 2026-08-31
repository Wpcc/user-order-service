package com.wpcc.userorderservice.order.dto;

import java.math.BigDecimal;

public record OrderPreview(
    Long userId,
    Long productId,
    String productName,
    BigDecimal productPrice,
    Integer quantity,
    BigDecimal totalAmount) {

}
