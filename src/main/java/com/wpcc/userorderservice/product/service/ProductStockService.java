package com.wpcc.userorderservice.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;

@Service
public class ProductStockService {
  private final ProductMapper productMapper;

  public ProductStockService(ProductMapper productMapper) {
    this.productMapper = productMapper;
  }

  @Transactional
  public void decreaseStock(long productId, int quantity) {
    if (quantity < 1) {
      throw new IllegalArgumentException("数量必须大于等于 1");
    }

    DatabaseProduct product = productMapper.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + productId));

    if (product.stock() < quantity) {
      throw new IllegalArgumentException("库存不足：" + productId);
    }

    int result = productMapper.decreaseStock(productId, quantity);
    if (result != 1) {
      throw new IllegalStateException("库存扣减失败：" + productId);
    }
  }
}
