package com.wpcc.userorderservice.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long productId,
    String productName,
    BigDecimal productPrice,
    Integer quantity,
    BigDecimal subtotalAmount) {

}
