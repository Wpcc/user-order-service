package com.wpcc.userorderservice.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
import com.wpcc.userorderservice.order.dto.CreateOrderRequest;
import com.wpcc.userorderservice.order.dto.CreateOrderResponse;
import com.wpcc.userorderservice.order.dto.OrderResponse;
import com.wpcc.userorderservice.order.mapper.OrderStatus;
import com.wpcc.userorderservice.order.service.OrderCreationService;
import com.wpcc.userorderservice.order.service.OrderQueryService;

class OrderControllerTest {

  private OrderCreationService orderCreationService;
  private OrderQueryService orderQueryService;
  private OrderController orderController;

  @BeforeEach
  void setUp() {
    orderCreationService = mock(OrderCreationService.class);
    orderQueryService = mock(OrderQueryService.class);
    orderController = new OrderController(orderCreationService, orderQueryService);
  }

  @Test
  void createsOrderAndReturnsCreatedResponse() {
    CreateOrderRequest request = new CreateOrderRequest(1L, 10L, 2);
    when(orderCreationService.create(request)).thenReturn(100L);

    ResponseEntity<CreateOrderResponse> response = orderController.createOrders(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(100L, response.getBody().orderId());
    verify(orderCreationService).create(request);
  }

  @Test
  void returnsOrderWhenItExists() {
    OrderResponse expected = new OrderResponse(
        100L, 1L, new BigDecimal("399.80"), OrderStatus.PENDING, List.of());
    when(orderQueryService.findById(100L)).thenReturn(Optional.of(expected));

    ResponseEntity<OrderResponse> response = orderController.findById(100L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expected, response.getBody());
  }

  @Test
  void throwsNotFoundWhenOrderDoesNotExist() {
    when(orderQueryService.findById(100L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> orderController.findById(100L));

    assertEquals("订单不存在：100", exception.getMessage());
  }
}
