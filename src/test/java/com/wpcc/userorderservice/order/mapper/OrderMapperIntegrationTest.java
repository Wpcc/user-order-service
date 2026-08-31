package com.wpcc.userorderservice.order.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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

  @Test
  void filtersOrdersByOptionalConditions() {
    DatabaseOrder existingOrder = orderMapper.findById(1L).orElseThrow();

    List<DatabaseOrder> allOrders = orderMapper.findByCondition(null, null);
    assertTrue(allOrders.stream().anyMatch(order -> order.id().equals(1L)));
    assertTrue(allOrders.stream()
        .allMatch(order -> order.id() != null));
    assertTrue(IntStream.range(1, allOrders.size())
        .allMatch(index -> allOrders.get(index - 1).id() > allOrders.get(index).id()));

    List<DatabaseOrder> userOrders = orderMapper.findByCondition(existingOrder.userId(), null);
    assertFalse(userOrders.isEmpty());
    assertTrue(userOrders.stream()
        .allMatch(order -> order.userId().equals(existingOrder.userId())));

    List<DatabaseOrder> statusOrders = orderMapper.findByCondition(null, existingOrder.status());
    assertFalse(statusOrders.isEmpty());
    assertTrue(statusOrders.stream()
        .allMatch(order -> order.status() == existingOrder.status()));

    List<DatabaseOrder> matchedOrders = orderMapper.findByCondition(
        existingOrder.userId(), existingOrder.status());
    assertFalse(matchedOrders.isEmpty());
    assertTrue(matchedOrders.stream().allMatch(order ->
        order.userId().equals(existingOrder.userId())
            && order.status() == existingOrder.status()));
  }

  @Test
  void returnsSortedOrderPages() {
    List<DatabaseOrder> descendingPage = orderMapper.findPage(
        10, 0, OrderSortField.ID, SortDirection.DESC);
    assertFalse(descendingPage.isEmpty());
    assertTrue(descendingPage.size() <= 10);
    assertTrue(IntStream.range(1, descendingPage.size())
        .allMatch(index -> descendingPage.get(index - 1).id() > descendingPage.get(index).id()));

    List<DatabaseOrder> ascendingPage = orderMapper.findPage(
        10, 0, OrderSortField.ID, SortDirection.ASC);
    assertFalse(ascendingPage.isEmpty());
    assertTrue(IntStream.range(1, ascendingPage.size())
        .allMatch(index -> ascendingPage.get(index - 1).id() < ascendingPage.get(index).id()));
  }
}
