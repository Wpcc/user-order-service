package com.wpcc.userorderservice.order.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class OrderMapperIntegrationTest {

  @Autowired
  private OrderMapper orderMapper;

  @Test
  void findsExistingOrderAndItsMigratedItems() {
    Optional<DatabaseOrder> order = orderMapper.findById(1L);

    assertTrue(order.isPresent());
    assertEquals(1L, order.orElseThrow().id());

    List<DatabaseOrderItem> items = orderMapper.findItemsByOrderId(1L);
    assertFalse(items.isEmpty());
    assertTrue(items.stream().allMatch(item -> item.orderId().equals(1L)));
  }
}
