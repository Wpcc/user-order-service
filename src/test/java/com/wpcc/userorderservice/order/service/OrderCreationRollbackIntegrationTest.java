package com.wpcc.userorderservice.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import com.wpcc.userorderservice.order.dto.CreateOrderRequest;
import com.wpcc.userorderservice.order.mapper.OrderInsertCommand;
import com.wpcc.userorderservice.order.mapper.OrderMapper;
import com.wpcc.userorderservice.product.mapper.DatabaseProduct;
import com.wpcc.userorderservice.product.mapper.ProductMapper;

@SpringBootTest
@ActiveProfiles("local")
@Import(OrderCreationRollbackIntegrationTest.FailingOrderMapperConfiguration.class)
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class OrderCreationRollbackIntegrationTest {

  @Autowired
  private OrderCreationService orderCreationService;

  @Autowired
  private ProductMapper productMapper;

  @Test
  void rollsBackStockWhenOrderInsertFails() {
    DatabaseProduct productBefore = productMapper.findById(1L).orElseThrow();

    assertThrows(IllegalStateException.class,
        () -> orderCreationService.create(new CreateOrderRequest(1L, 1L, 1)));

    DatabaseProduct productAfter = productMapper.findById(1L).orElseThrow();
    assertEquals(productBefore.stock(), productAfter.stock());
  }

  @TestConfiguration(proxyBeanMethods = false)
  static class FailingOrderMapperConfiguration {

    @Bean
    @Primary
    OrderMapper failingOrderMapper() {
      OrderMapper orderMapper = mock(OrderMapper.class);
      when(orderMapper.insert(any(OrderInsertCommand.class))).thenReturn(0);
      return orderMapper;
    }
  }
}
