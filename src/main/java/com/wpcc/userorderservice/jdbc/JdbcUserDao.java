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

  public long save(String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("用户名不能为空");
    }

    String sql = "INSERT INTO users (username) VALUES (?)";

    try (Connection connection = DriverManager.getConnection(url, this.username, password);
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, username);

      int affectedRows = statement.executeUpdate();
      if (affectedRows == 0) {
        throw new JdbcDataAccessException("保存用户失败", null);
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getLong(1);
        }
      }

      throw new JdbcDataAccessException("保存用户失败", null);
    } catch (SQLException e) {
      throw new JdbcDataAccessException("保存用户失败", e);
    }
  }

  public Optional<JdbcUser> findById(long id) {
    if (id <= 0) {
      throw new IllegalArgumentException("id 必须大于 0");
    }

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
    } catch (SQLException e) {
      throw new JdbcDataAccessException("按 id 查询用户失败", e);
    }
  }

  public Optional<JdbcUser> findByUsername(String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("用户名不能为空");
    }

    String sql = "SELECT id, username FROM users WHERE username = ?";

    try (Connection connection = DriverManager.getConnection(url, this.username, password);
        PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setString(1, username);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(new JdbcUser(
              resultSet.getLong("id"),
              resultSet.getString("username")));
        }

        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new JdbcDataAccessException("按用户名查询用户失败", e);
    }
  }
}
