package com.wpcc.userorderservice.user.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("""
        SELECT id,username
        FROM users
        WHERE id = #{id}
      """)
  Optional<DatabaseUser> findById(long id);

  @Insert("""
      INSERT INTO users (username)
      VALUES (#{username})
      """)
  int insert(@Param("username") String username);

  @Select("""
      SELECT id,username
      FROM users
      ORDER BY id
      """)
  List<DatabaseUser> findAll();
}
