package com.wpcc.userorderservice.user;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final Map<Long, User> users = new ConcurrentHashMap<>();
  private final AtomicLong nextId = new AtomicLong(1);

  public User save(String username) {
    long id = nextId.getAndIncrement();
    User user = new User(id, username);
    users.put(id, user);
    return user;
  }

  public Optional<User> findById(long id) {
    return Optional.ofNullable(users.get(id));
  }
}
