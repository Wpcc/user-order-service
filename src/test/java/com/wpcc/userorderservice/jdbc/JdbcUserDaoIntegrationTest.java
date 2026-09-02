package com.wpcc.userorderservice.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    String url = System.getenv("TEST_DB_URL");
    String databaseUsername = System.getenv("TEST_DB_USERNAME");
    String password = System.getenv("TEST_DB_PASSWORD");

    Assumptions.assumeTrue(hasText(url) && hasText(databaseUsername) && hasText(password),
        "未配置数据库环境变量，跳过 JDBC 集成测试。");

    JdbcUserDao userDao = new JdbcUserDao(url, databaseUsername, password);
    String username = "jdbc-test-" + System.currentTimeMillis();
    long id = userDao.save(username);

    try {
      Optional<JdbcUser> savedUser = userDao.findById(id);
      Optional<JdbcUser> userFoundByUsername = userDao.findByUsername(username);

      assertTrue(savedUser.isPresent());
      assertEquals(id, savedUser.orElseThrow().id());
      assertEquals(username, savedUser.orElseThrow().username());
      assertTrue(userFoundByUsername.isPresent());
      assertEquals(id, userFoundByUsername.orElseThrow().id());
    } finally {
      deleteUser(url, databaseUsername, password, id);
    }
  }

  @Test
  void rejectsInvalidParametersBeforeConnectingToDatabase() {
    JdbcUserDao userDao = new JdbcUserDao("invalid-url", "unused", "unused");

    assertThrows(IllegalArgumentException.class, () -> userDao.save(" "));
    assertThrows(IllegalArgumentException.class, () -> userDao.findById(0));
    assertThrows(IllegalArgumentException.class, () -> userDao.findByUsername(null));
  }

  @Test
  void wrapsSqlExceptionAsDataAccessException() {
    JdbcUserDao userDao = new JdbcUserDao("invalid-url", "unused", "unused");

    JdbcDataAccessException exception = assertThrows(JdbcDataAccessException.class,
        () -> userDao.findById(1));

    assertEquals("按 id 查询用户失败", exception.getMessage());
    assertTrue(exception.getCause() instanceof SQLException);
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
