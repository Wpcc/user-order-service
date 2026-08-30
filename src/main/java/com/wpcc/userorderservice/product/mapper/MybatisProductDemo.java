package com.wpcc.userorderservice.product.mapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class MybatisProductDemo implements CommandLineRunner {
  private final ProductMapper productMapper;

  public MybatisProductDemo(ProductMapper productMapper) {
    this.productMapper = productMapper;
  }

  @Override
  public void run(String... args) {
    productMapper.findById(1L)
        .ifPresentOrElse(System.out::println, () -> System.out.println("未找到 id=1 的商品"));

    productMapper.findPage(10, 0)
        .forEach(System.out::println);

  }
}
