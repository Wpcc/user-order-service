package com.wpcc.userorderservice.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class JdbcUserDaoIntegrationTest {

  @Test
  void savesAndFindsUserWhenDatabaseEnvironmentIsConfigured() throws SQLException {
    String url = System.getenv("DB_URL");
    String databaseUsername = System.getenv("DB_USERNAME");
    String password = System.getenv("DB_PASSWORD");

    Assumptions.assumeTrue(hasText(url) && hasText(databaseUsername) && hasText(password),
        "未配置数据库环境变量，跳过 JDBC 集成测试。");

    JdbcUserDao userDao = new JdbcUserDao(url, databaseUsername, password);
    String username = "jdbc-test-" + System.currentTimeMillis();
    long id = userDao.save(username);

    try {
      Optional<JdbcUser> savedUser = userDao.findById(id);

      assertTrue(savedUser.isPresent());
      assertEquals(id, savedUser.orElseThrow().id());
      assertEquals(username, savedUser.orElseThrow().username());
    } finally {
      deleteUser(url, databaseUsername, password, id);
    }
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private static void deleteUser(String url, String username, String password, long id)
      throws SQLException {
    try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
      statement.setLong(1, id);
      statement.executeUpdate();
    }
  }
}
