package com.wpcc.userorderservice.order.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wpcc.userorderservice.order.dto.OrderItemResponse;
import com.wpcc.userorderservice.order.dto.OrderResponse;
import com.wpcc.userorderservice.order.mapper.DatabaseOrder;
import com.wpcc.userorderservice.order.mapper.OrderMapper;

@Service
public class OrderQueryService {
  private final OrderMapper orderMapper;

  public OrderQueryService(OrderMapper orderMapper) {
    this.orderMapper = orderMapper;
  }

  public Optional<OrderResponse> findById(long orderId) {
    return orderMapper.findById(orderId)
        .map(this::toOrderResponse);
  }

  public List<OrderResponse> findByUserId(long userId) {
    List<DatabaseOrder> listOrder = orderMapper.findByCondition(userId, null);
    return listOrder.stream()
        .map(this::toOrderResponse).toList();
  }

  private OrderResponse toOrderResponse(DatabaseOrder order) {
    List<OrderItemResponse> items = orderMapper.findItemsByOrderId(order.id())
        .stream()
        .map(item -> new OrderItemResponse(
            item.productId(),
            item.productName(),
            item.productPrice(),
            item.quantity(),
            item.subtotalAmount()))
        .toList();

    return new OrderResponse(
        order.id(),
        order.userId(),
        order.totalAmount(),
        order.status(),
        items);
  }

}
