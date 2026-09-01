package com.wpcc.userorderservice.order.dto;

import java.math.BigDecimal;
import java.util.List;

import com.wpcc.userorderservice.order.mapper.OrderStatus;

public record OrderResponse(
    Long id,
    Long userId,
    BigDecimal totalAmount,
    OrderStatus status,
    List<OrderItemResponse> items) {

}
