package com.wpcc.userorderservice.user.mapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class MybatisUserDemo implements CommandLineRunner {
  private final UserMapper userMapper;

  public MybatisUserDemo(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public void run(String... args) {
    userMapper.findById(1L)
        .ifPresentOrElse(System.out::println, () -> System.out.println("未找到 id=1 的用户"));

    userMapper.findAll()
        .forEach(System.out::println);
  }
}
