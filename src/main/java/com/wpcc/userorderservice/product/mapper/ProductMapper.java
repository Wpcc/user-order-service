package com.wpcc.userorderservice.product.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {
  @Insert("""
      INSERT INTO products (name,price,stock)
      VALUES(#{name},#{price},#{stock})
      """)
  int insert(@Param("name") String name, @Param("price") BigDecimal price, @Param("stock") int stock);

  @Select("""
      SELECT id,name,price,stock
      FROM products
      WHERE id = #{id}
      """)
  Optional<DatabaseProduct> findById(long id);

  @Select("""
      SELECT id,name,price,stock
      FROM products
      ORDER BY id
      LIMIT #{limit} OFFSET #{offset}
      """)
  List<DatabaseProduct> findPage(@Param(value = "limit") int limit, @Param("offset") int offset);

  @Update("""
        UPDATE products
        SET stock = stock - #{quantity}
        WHERE id = #{id}
          AND stock >= #{quantity}
      """)
  int decreaseStock(@Param("id") long id, @Param("quantity") int quantity);
}
