package com.wpcc.userorderservice.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wpcc.userorderservice.common.exception.InsufficientStockException;
import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;

class ProductStockServiceTest {

  private ProductMapper productMapper;
  private ProductStockService productStockService;

  @BeforeEach
  void setUp() {
    productMapper = mock(ProductMapper.class);
    productStockService = new ProductStockService(productMapper);
  }

  @Test
  void decreasesStockWhenQuantityIsAvailable() {
    when(productMapper.findById(10L)).thenReturn(Optional.of(productWithStock(5)));
    when(productMapper.decreaseStock(10L, 3)).thenReturn(1);

    productStockService.decreaseStock(10L, 3);

    verify(productMapper).decreaseStock(10L, 3);
  }

  @Test
  void rejectsInvalidQuantityBeforeAccessingDatabase() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> productStockService.decreaseStock(10L, 0));

    assertEquals("数量必须大于等于 1", exception.getMessage());
    verifyNoInteractions(productMapper);
  }

  @Test
  void rejectsMissingProduct() {
    when(productMapper.findById(10L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> productStockService.decreaseStock(10L, 1));

    assertEquals("商品不存在：10", exception.getMessage());
    verify(productMapper, never()).decreaseStock(10L, 1);
  }

  @Test
  void rejectsInsufficientStock() {
    when(productMapper.findById(10L)).thenReturn(Optional.of(productWithStock(2)));

    assertThrows(InsufficientStockException.class,
        () -> productStockService.decreaseStock(10L, 3));

    verify(productMapper, never()).decreaseStock(10L, 3);
  }

  @Test
  void rejectsFailedStockUpdate() {
    when(productMapper.findById(10L)).thenReturn(Optional.of(productWithStock(5)));
    when(productMapper.decreaseStock(10L, 3)).thenReturn(0);

    InsufficientStockException exception = assertThrows(InsufficientStockException.class,
        () -> productStockService.decreaseStock(10L, 3));

    assertEquals("库存不足：10", exception.getMessage());
  }

  private DatabaseProduct productWithStock(int stock) {
    return new DatabaseProduct(10L, "Keyboard", new BigDecimal("199.90"), stock);
  }
}
