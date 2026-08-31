package com.wpcc.userorderservice.order.mapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class MybatisOrderDemo implements CommandLineRunner {
  private final OrderMapper orderMapper;

  public MybatisOrderDemo(OrderMapper orderMapper) {
    this.orderMapper = orderMapper;
  }

  @Override
  public void run(String... args) {
    orderMapper.findById(1)
        .ifPresentOrElse(System.out::println, () -> {
          System.out.println("未找到 id=1 的订单");
        });

    orderMapper.findItemsByOrderId(1)
        .forEach(System.out::println);

    orderMapper.findByCondition(1L, OrderStatus.PENDING)
        .forEach(System.out::println);

    System.out.println("------- 分页打印数据---------");
    OrderPageRequest request = new OrderPageRequest(1, 10, OrderSortField.CREATED_AT, SortDirection.DESC);
    orderMapper.findPage(
        request.size(),
        request.offset(),
        request.sortField(),
        request.direction())
        .forEach(System.out::println);
    System.out.println("------- 分页打印数据---------");
  }
}
