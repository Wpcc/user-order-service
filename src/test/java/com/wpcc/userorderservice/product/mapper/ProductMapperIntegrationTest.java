package com.wpcc.userorderservice.product.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
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
class ProductMapperIntegrationTest {

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private DataSource dataSource;

  @Test
  void insertsFindsAndPagesProduct() throws SQLException {
    String name = "mybatis-product-test-" + System.currentTimeMillis();
    BigDecimal price = new BigDecimal("12.34");

    try {
      assertEquals(1, productMapper.insert(name, price, 7));

      List<DatabaseProduct> products = productMapper.findPage(100, 0);
      DatabaseProduct insertedProduct = products.stream()
          .filter(product -> name.equals(product.name()))
          .findFirst()
          .orElseThrow();

      Optional<DatabaseProduct> productFoundById = productMapper.findById(insertedProduct.id());
      assertTrue(productFoundById.isPresent());
      assertEquals(price, productFoundById.orElseThrow().price());
      assertEquals(7, productFoundById.orElseThrow().stock());
    } finally {
      deleteProductByName(name);
    }
  }

  @Test
  void preventsStockFromBecomingNegative() throws SQLException {
    String name = "mybatis-stock-test-" + System.currentTimeMillis();

    try {
      assertEquals(1, productMapper.insert(name, new BigDecimal("1.00"), 1));

      DatabaseProduct product = productMapper.findPage(100, 0).stream()
          .filter(candidate -> name.equals(candidate.name()))
          .findFirst()
          .orElseThrow();

      assertEquals(1, productMapper.decreaseStock(product.id(), 1));
      assertEquals(0, productMapper.decreaseStock(product.id(), 1));
      assertEquals(0, productMapper.findById(product.id()).orElseThrow().stock());
    } finally {
      deleteProductByName(name);
    }
  }

  private void deleteProductByName(String name) throws SQLException {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM products WHERE name = ?")) {
      statement.setString(1, name);
      statement.executeUpdate();
    }
  }
}
