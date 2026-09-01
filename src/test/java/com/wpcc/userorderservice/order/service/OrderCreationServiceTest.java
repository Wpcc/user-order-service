package com.wpcc.userorderservice.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
import com.wpcc.userorderservice.order.dto.CreateOrderRequest;
import com.wpcc.userorderservice.order.dto.OrderPreview;
import com.wpcc.userorderservice.order.mapper.OrderInsertCommand;
import com.wpcc.userorderservice.order.mapper.OrderMapper;
import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;
import com.wpcc.userorderservice.product.service.ProductStockService;
import com.wpcc.userorderservice.user.mapper.DatabaseUser;
import com.wpcc.userorderservice.user.mapper.UserMapper;

class OrderCreationServiceTest {

  private UserMapper userMapper;
  private ProductMapper productMapper;
  private OrderMapper orderMapper;
  private ProductStockService productStockService;
  private OrderCreationService orderCreationService;

  @BeforeEach
  void setUp() {
    userMapper = mock(UserMapper.class);
    productMapper = mock(ProductMapper.class);
    orderMapper = mock(OrderMapper.class);
    productStockService = mock(ProductStockService.class);
    orderCreationService = new OrderCreationService(
        userMapper, productMapper, orderMapper, productStockService);
  }

  @Test
  void createsPreviewWithCalculatedTotalAmount() {
    when(userMapper.findById(1L)).thenReturn(Optional.of(new DatabaseUser(1L, "alice")));
    when(productMapper.findById(10L)).thenReturn(Optional.of(
        new DatabaseProduct(10L, "Keyboard", new BigDecimal("199.90"), 20)));

    OrderPreview preview = orderCreationService.preview(new CreateOrderRequest(1L, 10L, 3));

    assertEquals(1L, preview.userId());
    assertEquals(10L, preview.productId());
    assertEquals("Keyboard", preview.productName());
    assertEquals(new BigDecimal("199.90"), preview.productPrice());
    assertEquals(3, preview.quantity());
    assertEquals(new BigDecimal("599.70"), preview.totalAmount());
    verify(userMapper).findById(1L);
    verify(productMapper).findById(10L);
  }

  @Test
  void rejectsPreviewWhenUserDoesNotExist() {
    when(userMapper.findById(1L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> orderCreationService.preview(new CreateOrderRequest(1L, 10L, 1)));

    assertEquals("用户不存在：1", exception.getMessage());
    verifyNoInteractions(productMapper);
  }

  @Test
  void rejectsPreviewWhenProductDoesNotExist() {
    when(userMapper.findById(1L)).thenReturn(Optional.of(new DatabaseUser(1L, "alice")));
    when(productMapper.findById(10L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> orderCreationService.preview(new CreateOrderRequest(1L, 10L, 1)));

    assertEquals("商品不存在：10", exception.getMessage());
  }

  @Test
  void createsOrderAndOrderItemAfterDecreasingStock() {
    prepareValidOrder();
    when(orderMapper.insert(any(OrderInsertCommand.class))).thenAnswer(invocation -> {
      OrderInsertCommand command = invocation.getArgument(0);
      command.setId(100L);
      return 1;
    });
    when(orderMapper.insertItem(any())).thenReturn(1);

    long orderId = orderCreationService.create(new CreateOrderRequest(1L, 10L, 3));

    assertEquals(100L, orderId);
    verify(productStockService).decreaseStock(10L, 3);
    verify(orderMapper).insert(any(OrderInsertCommand.class));
    verify(orderMapper).insertItem(any());
  }

  @Test
  void rejectsOrderCreationWhenOrderInsertFails() {
    prepareValidOrder();
    when(orderMapper.insert(any(OrderInsertCommand.class))).thenReturn(0);

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> orderCreationService.create(new CreateOrderRequest(1L, 10L, 3)));

    assertEquals("创建订单失败", exception.getMessage());
    verify(productStockService).decreaseStock(10L, 3);
    verify(orderMapper, never()).insertItem(any());
  }

  @Test
  void rejectsOrderCreationWhenOrderItemInsertFails() {
    prepareValidOrder();
    when(orderMapper.insert(any(OrderInsertCommand.class))).thenAnswer(invocation -> {
      OrderInsertCommand command = invocation.getArgument(0);
      command.setId(100L);
      return 1;
    });
    when(orderMapper.insertItem(any())).thenReturn(0);

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> orderCreationService.create(new CreateOrderRequest(1L, 10L, 3)));

    assertEquals("创建订单详情失败", exception.getMessage());
  }

  private void prepareValidOrder() {
    when(userMapper.findById(1L)).thenReturn(Optional.of(new DatabaseUser(1L, "alice")));
    when(productMapper.findById(10L)).thenReturn(Optional.of(
        new DatabaseProduct(10L, "Keyboard", new BigDecimal("199.90"), 20)));
  }
}
