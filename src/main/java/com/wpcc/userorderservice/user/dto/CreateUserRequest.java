package com.wpcc.userorderservice.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank(message = "用户名不能为空") @Size(min = 2, max = 32, message = "用户名长度必须在 2 到 32 个字符之间") String username) {

}
