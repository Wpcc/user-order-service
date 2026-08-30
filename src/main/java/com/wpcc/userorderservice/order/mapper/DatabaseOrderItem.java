package com.wpcc.userorderservice.order.mapper;

import java.math.BigDecimal;

public record DatabaseOrderItem(
    Long id,
    Long orderId,
    Long productId,
    String productName,
    BigDecimal productPrice,
    Integer quantity,
    BigDecimal subtotalAmount) {

}
