package com.wpcc.userorderservice.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String getUsernameById(long id) {
    return userRepository.findUserNameById(id);
  }
}
