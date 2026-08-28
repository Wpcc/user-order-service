package com.wpcc.userorderservice.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class JdbcUserDao {
  private final String url;
  private final String username;
  private final String password;

  public JdbcUserDao(String url, String username, String password) {
    this.url = url;
    this.username = username;
    this.password = password;
  }

  public long save(String username) throws SQLException {
    String sql = "INSERT INTO users (username) VALUES (?)";

    try (Connection connection = DriverManager.getConnection(url, this.username, password);
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, username);

      int affectedRows = statement.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("新增用户失败，没有影响任何数据。");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getLong(1);
        }
      }

      throw new SQLException("新增用户成功，但没有获取到生成的 id。");
    }
  }

  public Optional<JdbcUser> findById(long id) throws SQLException {
    String sql = "SELECT id, username FROM users WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setLong(1, id);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          JdbcUser user = new JdbcUser(
              resultSet.getLong("id"),
              resultSet.getString("username"));
          return Optional.of(user);
        }

        return Optional.empty();
      }
    }
  }
}
