package com.wpcc.userorderservice.order.mapper;

import java.util.Objects;

public record OrderPageRequest(
    int page,
    int size,
    OrderSortField sortField,
    SortDirection direction) {
  public OrderPageRequest {
    if (page < 1) {
      throw new IllegalArgumentException("page 必须大于等于 1");
    }

    if (size < 1 || size > 100) {
      throw new IllegalArgumentException("size 必须在 1 到 100 之间");
    }

    Objects.requireNonNull(sortField, "sortField 不能为空");
    Objects.requireNonNull(direction, "direction 不能为空");
  }

  public int offset() {
    return (page - 1) * size;
  }
}
