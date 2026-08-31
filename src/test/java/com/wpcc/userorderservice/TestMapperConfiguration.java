package com.wpcc.userorderservice;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.wpcc.userorderservice.product.mapper.ProductMapper;
import com.wpcc.userorderservice.user.mapper.UserMapper;

@TestConfiguration(proxyBeanMethods = false)
public class TestMapperConfiguration {

  @Bean
  UserMapper userMapper() {
    return mock(UserMapper.class);
  }

  @Bean
  ProductMapper productMapper() {
    return mock(ProductMapper.class);
  }
}
