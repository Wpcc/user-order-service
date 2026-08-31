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

  @Select("""
      <script>
        SELECT id,user_id,total_amount,status
        FROM orders
        <where>
          <if test="userId != null">
            AND user_id = #{userId}
          </if>
          <if test="status != null">
            AND status = #{status}
          </if>
        </where>
        ORDER BY id DESC
      </script>
      """)
  List<DatabaseOrder> findByCondition(@Param("userId") Long userId, @Param("status") OrderStatus status);

  @Select("""
      <script>
        SELECT id,user_id,total_amount,status
        FROM orders
        ORDER BY
        <choose>
          <when test="sortField.name() == 'ID'">
            id
          </when>
          <when test="sortField.name() == 'TOTAL_AMOUNT'">
            total_amount
          </when>
          <when test="sortField.name() == 'CREATED_AT'">
            created_at
          </when>
          <otherwise>
            id
          </otherwise>
        </choose>
        <choose>
          <when test="direction.name() == 'ASC'">
              ASC
          </when>
          <otherwise>
              DESC
          </otherwise>
        </choose>
        LIMIT #{limit} OFFSET #{offset}
      </script>
      """)
  List<DatabaseOrder> findPage(
      @Param("limit") int limit,
      @Param("offset") int offset,
      @Param("sortField") OrderSortField sortField,
      @Param("direction") SortDirection direction);
}
