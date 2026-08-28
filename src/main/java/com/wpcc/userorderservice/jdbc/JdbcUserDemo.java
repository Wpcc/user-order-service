package com.wpcc.userorderservice.jdbc;

import java.sql.SQLException;

public class JdbcUserDemo {
  public static void main(String[] args) {
    JdbcUserDao userDao = new JdbcUserDao(
        requiredEnv("DB_URL"),
        requiredEnv("DB_USERNAME"),
        requiredEnv("DB_PASSWORD"));

    try {
      String username = "jdbc-user-" + System.currentTimeMillis();
      long id = userDao.save(username);
      System.out.println("新增用户 id：" + id);

      userDao.findById(id)
          .ifPresent(System.out::println);
    } catch (SQLException e) {
      System.err.println("数据库操作失败：" + e.getMessage());
      e.printStackTrace();
    }

  }

  private static String requiredEnv(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("缺少环境变量：" + name);
    }
    return value;
  }
}
