package com.wpcc.userorderservice.order.mapper;

import java.math.BigDecimal;

public record DatabaseOrder(
    Long id,
    Long userId,
    BigDecimal totalAmount,
    OrderStatus status) {

}
