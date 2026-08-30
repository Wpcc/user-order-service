package com.wpcc.userorderservice.order.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
  @Select("""
      SELECT id,user_id,total_amount,status
      FROM orders
      WHERE id = #{id}
      """)
  Optional<DatabaseOrder> findById(@Param("id") long id);

  @Select("""
      SELECT id,order_id,product_id,product_name,product_price,quantity,subtotal_amount
      FROM order_items
      WHERE order_id = #{orderId}
      ORDER BY id
      """)
  List<DatabaseOrderItem> findItemsByOrderId(@Param("orderId") long orderId);
}
