package com.wpcc.userorderservice.order.service;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
import com.wpcc.userorderservice.order.dto.CreateOrderRequest;
import com.wpcc.userorderservice.order.dto.OrderPreview;
import com.wpcc.userorderservice.order.mapper.OrderInsertCommand;
import com.wpcc.userorderservice.order.mapper.OrderItemInsertCommand;
import com.wpcc.userorderservice.order.mapper.OrderMapper;
import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;
import com.wpcc.userorderservice.product.service.ProductStockService;
import com.wpcc.userorderservice.user.mapper.UserMapper;

@Service
public class OrderCreationService {
  private final UserMapper userMapper;
  private final ProductMapper productMapper;
  private final OrderMapper orderMapper;
  private final ProductStockService productStockService;
  private static final Logger log = LoggerFactory.getLogger(OrderCreationService.class);

  public OrderCreationService(UserMapper userMapper, ProductMapper productMapper, OrderMapper orderMapper,
      ProductStockService productStockService) {
    this.userMapper = userMapper;
    this.productMapper = productMapper;
    this.orderMapper = orderMapper;
    this.productStockService = productStockService;
  }

  public OrderPreview preview(CreateOrderRequest request) {
    userMapper.findById(request.userId())
        .orElseThrow(() -> new ResourceNotFoundException("用户不存在：" + request.userId()));

    DatabaseProduct product = productMapper.findById(request.productId())
        .orElseThrow(() -> new ResourceNotFoundException("商品不存在：" + request.productId()));

    BigDecimal totalAmount = product.price()
        .multiply(BigDecimal.valueOf(request.quantity()));

    log.info(
        "开始创建订单，userId={}, productId={}, quantity={}",
        request.userId(),
        product.id(),
        request.quantity());

    return new OrderPreview(
        request.userId(),
        product.id(),
        product.name(),
        product.price(),
        request.quantity(),
        totalAmount);
  }

  @Transactional
  public long create(CreateOrderRequest request) {
    OrderPreview orderPreview = preview(request);

    productStockService.decreaseStock(orderPreview.productId(), orderPreview.quantity());

    OrderInsertCommand order = new OrderInsertCommand(null,
        orderPreview.userId(),
        orderPreview.productId(),
        orderPreview.productName(),
        orderPreview.productPrice(),
        orderPreview.quantity(),
        orderPreview.totalAmount());

    int affectRows = orderMapper.insert(order);
    if (affectRows != 1) {
      throw new IllegalStateException("创建订单失败");
    }

    if (order.getId() == null) {
      throw new IllegalStateException("订单主键回填失败");
    }

    OrderItemInsertCommand orderItem = new OrderItemInsertCommand(
        order.getId(),
        orderPreview.productId(),
        orderPreview.productName(),
        orderPreview.productPrice(),
        orderPreview.quantity(),
        orderPreview.totalAmount());

    int affectItemRows = orderMapper.insertItem(orderItem);
    if (affectItemRows != 1) {
      throw new IllegalStateException("创建订单详情失败");
    }

    log.info(
        "订单创建成功，orderId={}, userId={}, totalAmount={}",
        order.getId(),
        orderPreview.userId(),
        orderPreview.totalAmount());
    return order.getId();
  }
}
