package com.wpcc.userorderservice.common.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "用户订单服务 API", version = "v1", description = "用户与订单管理接口文档"))
public class OpenApiConfig {

}
