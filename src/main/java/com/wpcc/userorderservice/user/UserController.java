package com.wpcc.userorderservice.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wpcc.userorderservice.user.dto.CreateUserRequest;
import com.wpcc.userorderservice.user.dto.UserResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest user) {
    User createUser = userService.createUser(user.username());
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(createUser));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> findUserById(@PathVariable long id) {
    return userService.findUserById(id)
        .map(user -> ResponseEntity.ok(toResponse(user)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private UserResponse toResponse(User user) {
    return new UserResponse(user.id(), user.username());
  }

}
