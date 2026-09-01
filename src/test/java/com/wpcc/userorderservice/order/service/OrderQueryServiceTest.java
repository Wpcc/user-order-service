package com.wpcc.userorderservice.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wpcc.userorderservice.order.dto.OrderResponse;
import com.wpcc.userorderservice.order.mapper.DatabaseOrder;
import com.wpcc.userorderservice.order.mapper.DatabaseOrderItem;
import com.wpcc.userorderservice.order.mapper.OrderMapper;
import com.wpcc.userorderservice.order.mapper.OrderStatus;

class OrderQueryServiceTest {

  private OrderMapper orderMapper;
  private OrderQueryService orderQueryService;

  @BeforeEach
  void setUp() {
    orderMapper = mock(OrderMapper.class);
    orderQueryService = new OrderQueryService(orderMapper);
  }

  @Test
  void findsOrderWithItsItemsById() {
    DatabaseOrder order = order(100L, 1L);
    when(orderMapper.findById(100L)).thenReturn(Optional.of(order));
    when(orderMapper.findItemsByOrderId(100L)).thenReturn(List.of(item(100L, 10L)));

    OrderResponse response = orderQueryService.findById(100L).orElseThrow();

    assertEquals(100L, response.id());
    assertEquals(1L, response.userId());
    assertEquals(OrderStatus.PENDING, response.status());
    assertEquals(1, response.items().size());
    assertEquals("Keyboard", response.items().getFirst().productName());
  }

  @Test
  void returnsEmptyOptionalWhenOrderDoesNotExist() {
    when(orderMapper.findById(100L)).thenReturn(Optional.empty());

    Optional<OrderResponse> response = orderQueryService.findById(100L);

    assertTrue(response.isEmpty());
    verify(orderMapper, never()).findItemsByOrderId(100L);
  }

  @Test
  void findsAllOrdersForUser() {
    DatabaseOrder firstOrder = order(100L, 1L);
    DatabaseOrder secondOrder = order(101L, 1L);
    when(orderMapper.findByCondition(1L, null)).thenReturn(List.of(firstOrder, secondOrder));
    when(orderMapper.findItemsByOrderId(100L)).thenReturn(List.of(item(100L, 10L)));
    when(orderMapper.findItemsByOrderId(101L)).thenReturn(List.of(item(101L, 11L)));

    List<OrderResponse> responses = orderQueryService.findByUserId(1L);

    assertEquals(2, responses.size());
    assertTrue(responses.stream().allMatch(response -> response.userId().equals(1L)));
    assertFalse(responses.getFirst().items().isEmpty());
    verify(orderMapper).findByCondition(1L, null);
  }

  private DatabaseOrder order(long id, long userId) {
    return new DatabaseOrder(id, userId, new BigDecimal("199.90"), OrderStatus.PENDING);
  }

  private DatabaseOrderItem item(long orderId, long productId) {
    return new DatabaseOrderItem(
        1L, orderId, productId, "Keyboard", new BigDecimal("199.90"), 1,
        new BigDecimal("199.90"));
  }
}
