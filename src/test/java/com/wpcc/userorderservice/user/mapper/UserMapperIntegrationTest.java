package com.wpcc.userorderservice.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration")
@EnabledIfEnvironmentVariable(named = "TEST_DB_URL", matches = ".+")
class UserMapperIntegrationTest {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private DataSource dataSource;

  @Test
  void findsExistingUserById() {
    Optional<DatabaseUser> user = userMapper.findById(1L);

    assertTrue(user.isPresent());
    assertEquals(1L, user.orElseThrow().id());
    assertTrue(!user.orElseThrow().username().isBlank());
  }

  @Test
  void insertsAndFindsUser() throws SQLException {
    String username = "mybatis-test-" + System.currentTimeMillis();

    try {
      assertEquals(1, userMapper.insert(username));

      List<DatabaseUser> users = userMapper.findAll();
      DatabaseUser insertedUser = users.stream()
          .filter(user -> username.equals(user.username()))
          .findFirst()
          .orElseThrow();

      Optional<DatabaseUser> userFoundById = userMapper.findById(insertedUser.id());
      assertTrue(userFoundById.isPresent());
      assertEquals(username, userFoundById.orElseThrow().username());
    } finally {
      deleteUserByUsername(username);
    }
  }

  private void deleteUserByUsername(String username) throws SQLException {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM users WHERE username = ?")) {
      statement.setString(1, username);
      statement.executeUpdate();
    }
  }
}
