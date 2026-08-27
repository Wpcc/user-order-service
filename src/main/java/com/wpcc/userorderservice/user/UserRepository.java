package com.wpcc.userorderservice.user;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  public String findUserNameById(long id) {
    return "user-" + id;
  }
}
