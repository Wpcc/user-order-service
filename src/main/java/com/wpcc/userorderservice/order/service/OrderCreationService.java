package com.wpcc.userorderservice.order.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.wpcc.userorderservice.order.dto.CreateOrderRequest;
import com.wpcc.userorderservice.order.dto.OrderPreview;
import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;
import com.wpcc.userorderservice.user.mapper.UserMapper;

@Service
public class OrderCreationService {
  private final UserMapper userMapper;
  private final ProductMapper productMapper;

  public OrderCreationService(UserMapper userMapper, ProductMapper productMapper) {
    this.userMapper = userMapper;
    this.productMapper = productMapper;
  }

  public OrderPreview preview(CreateOrderRequest request) {
    userMapper.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException("用户不存在：" + request.userId()));

    DatabaseProduct product = productMapper.findById(request.productId())
        .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + request.productId()));

    BigDecimal totalAmount = product.price()
        .multiply(BigDecimal.valueOf(request.quantity()));

    return new OrderPreview(
        request.userId(),
        product.id(),
        product.name(),
        product.price(),
        request.quantity(),
        totalAmount);
  }
}
