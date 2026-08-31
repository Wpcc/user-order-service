package com.wpcc.userorderservice.order.mapper;

import java.math.BigDecimal;

public record OrderItemInsertCommand(
    Long orderId,
    Long productId,
    String productName,
    BigDecimal productPrice,
    Integer quantity,
    BigDecimal subtotalAmount) {

}
