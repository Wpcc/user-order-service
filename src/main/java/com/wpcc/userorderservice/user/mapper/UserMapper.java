package com.wpcc.userorderservice.user.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("""
        SELECT id,username
        FROM users
        WHERE id = #{id}
      """)
  Optional<DatabaseUser> findById(long id);
}
