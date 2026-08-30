package com.wpcc.userorderservice.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class UserMapperIntegrationTest {

  @Autowired
  private UserMapper userMapper;

  @Test
  void findsExistingUserById() {
    Optional<DatabaseUser> user = userMapper.findById(1L);

    assertTrue(user.isPresent());
    assertEquals(1L, user.orElseThrow().id());
    assertTrue(!user.orElseThrow().username().isBlank());
  }
}
