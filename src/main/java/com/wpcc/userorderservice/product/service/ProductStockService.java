package com.wpcc.userorderservice.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wpcc.userorderservice.common.exception.InsufficientStockException;
import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;

@Service
public class ProductStockService {
  private final ProductMapper productMapper;
  private static final Logger log = LoggerFactory.getLogger(ProductStockService.class);

  public ProductStockService(ProductMapper productMapper) {
    this.productMapper = productMapper;
  }

  @Transactional
  public void decreaseStock(long productId, int quantity) {
    if (quantity < 1) {
      throw new IllegalArgumentException("数量必须大于等于 1");
    }

    DatabaseProduct product = productMapper.findById(productId)
        .orElseThrow(() -> {
          log.warn("商品不存在，productId={}", productId);
          return new ResourceNotFoundException("商品不存在：" + productId);
        });

    if (product.stock() < quantity) {
      log.warn("库存不足，productId={}, quantity={}, currentStock={}",
          productId, quantity, product.stock());
      throw new InsufficientStockException("库存不足：" + productId);
    }

    int result = productMapper.decreaseStock(productId, quantity);
    if (result != 1) {
      log.warn("库存不足，productId={}, quantity={}, currentStock={}",
          productId, quantity, product.stock());
      throw new InsufficientStockException("库存不足：" + productId);
    }
  }
}
