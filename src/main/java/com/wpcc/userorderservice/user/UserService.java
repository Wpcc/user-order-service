package com.wpcc.userorderservice.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(String username) {
    return userRepository.save(username);
  }

  public Optional<User> findUserById(long id) {
    return userRepository.findById(id);
  }
}
