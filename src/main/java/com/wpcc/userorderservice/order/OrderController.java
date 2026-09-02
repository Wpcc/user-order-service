package com.wpcc.userorderservice.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wpcc.userorderservice.common.exception.ResourceNotFoundException;
import com.wpcc.userorderservice.order.dto.CreateOrderRequest;
import com.wpcc.userorderservice.order.dto.CreateOrderResponse;
import com.wpcc.userorderservice.order.dto.OrderResponse;
import com.wpcc.userorderservice.order.service.OrderCreationService;
import com.wpcc.userorderservice.order.service.OrderQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单创建与查询接口")
public class OrderController {
  private final OrderCreationService orderCreationService;
  private final OrderQueryService orderQueryService;

  public OrderController(OrderCreationService orderCreationService, OrderQueryService orderQueryService) {
    this.orderCreationService = orderCreationService;
    this.orderQueryService = orderQueryService;
  }

  @Operation(summary = "创建订单")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "订单创建成功"),
      @ApiResponse(responseCode = "400", description = "请求参数不合法"),
      @ApiResponse(responseCode = "404", description = "用户或商品不存在"),
      @ApiResponse(responseCode = "409", description = "库存不足")
  })
  @PostMapping
  public ResponseEntity<CreateOrderResponse> createOrders(@Valid @RequestBody CreateOrderRequest request) {
    long orderId = orderCreationService.create(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreateOrderResponse(orderId));
  }

  @Operation(summary = "按 ID 查询订单")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "订单不存在")
  })
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> findById(@PathVariable long id) {
    OrderResponse response = orderQueryService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("订单不存在：" + id));

    return ResponseEntity.ok(response);
  }

}
