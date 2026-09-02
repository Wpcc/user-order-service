package com.wpcc.userorderservice.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
import com.wpcc.userorderservice.user.dto.CreateUserRequest;
import com.wpcc.userorderservice.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户创建与查询接口")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "创建用户", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "待创建的用户", required = true, content = @Content(examples = @ExampleObject(value = "{\"username\":\"alice\"}"))))
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "请求参数不合法")
  })
  @PostMapping
  public ResponseEntity<UserResponse> createUser(
      @Valid @RequestBody CreateUserRequest user) {
    User createUser = userService.createUser(user.username());
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(createUser));
  }

  @Operation(summary = "按 ID 查询用户")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> findUserById(@PathVariable long id) {
    return userService.findUserById(id)
        .map(user -> ResponseEntity.ok(toResponse(user)))
        .orElseThrow(() -> new ResourceNotFoundException("用户不存在：" + id));
  }

  private UserResponse toResponse(User user) {
    return new UserResponse(user.id(), user.username());
  }

}
