package com.wpcc.userorderservice.order.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OrderPageRequestTest {

  @Test
  void calculatesOffsetFromPageAndSize() {
    OrderPageRequest firstPage = new OrderPageRequest(
        1, 10, OrderSortField.ID, SortDirection.ASC);
    OrderPageRequest request = new OrderPageRequest(
        3, 10, OrderSortField.CREATED_AT, SortDirection.DESC);

    assertEquals(0, firstPage.offset());
    assertEquals(20, request.offset());
  }

  @Test
  void rejectsInvalidPageAndSize() {
    assertThrows(IllegalArgumentException.class,
        () -> new OrderPageRequest(0, 10, OrderSortField.ID, SortDirection.ASC));
    assertThrows(IllegalArgumentException.class,
        () -> new OrderPageRequest(1, 0, OrderSortField.ID, SortDirection.ASC));
    assertThrows(IllegalArgumentException.class,
        () -> new OrderPageRequest(1, 101, OrderSortField.ID, SortDirection.ASC));
  }

  @Test
  void rejectsMissingSortOptions() {
    assertThrows(NullPointerException.class,
        () -> new OrderPageRequest(1, 10, null, SortDirection.ASC));
    assertThrows(NullPointerException.class,
        () -> new OrderPageRequest(1, 10, OrderSortField.ID, null));
  }
}
