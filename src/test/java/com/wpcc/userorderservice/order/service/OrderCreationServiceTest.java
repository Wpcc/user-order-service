package com.wpcc.userorderservice.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wpcc.userorderservice.order.dto.CreateOrderRequest;
import com.wpcc.userorderservice.order.dto.OrderPreview;
import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;
import com.wpcc.userorderservice.user.mapper.DatabaseUser;
import com.wpcc.userorderservice.user.mapper.UserMapper;

class OrderCreationServiceTest {

  private UserMapper userMapper;
  private ProductMapper productMapper;
  private OrderCreationService orderCreationService;

  @BeforeEach
  void setUp() {
    userMapper = mock(UserMapper.class);
    productMapper = mock(ProductMapper.class);
    orderCreationService = new OrderCreationService(userMapper, productMapper);
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

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> orderCreationService.preview(new CreateOrderRequest(1L, 10L, 1)));

    assertEquals("用户不存在：1", exception.getMessage());
    verifyNoInteractions(productMapper);
  }

  @Test
  void rejectsPreviewWhenProductDoesNotExist() {
    when(userMapper.findById(1L)).thenReturn(Optional.of(new DatabaseUser(1L, "alice")));
    when(productMapper.findById(10L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> orderCreationService.preview(new CreateOrderRequest(1L, 10L, 1)));

    assertEquals("商品不存在：10", exception.getMessage());
  }
}
