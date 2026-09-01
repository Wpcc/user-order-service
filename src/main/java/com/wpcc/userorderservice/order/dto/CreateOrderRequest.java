package com.wpcc.userorderservice.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull(message = "用户 ID 不能为空") Long userId,
    @NotNull(message = "商品 ID 不能为空") Long productId,
    @NotNull(message = "购买数量不能为空") @Min(value = 1, message = "购买数量必须大于等于 1") Integer quantity) {

}
