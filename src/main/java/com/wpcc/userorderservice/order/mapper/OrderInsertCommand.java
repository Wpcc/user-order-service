package com.wpcc.userorderservice.order.mapper;

import java.math.BigDecimal;

public class OrderInsertCommand {
  private Long id;
  private Long userId;
  private Long productId;
  private String productName;
  private BigDecimal productPrice;
  private Integer quantity;
  private BigDecimal totalAmount;

  public OrderInsertCommand(Long id, Long userId, Long productId, String productName, BigDecimal productPrice,
      Integer quantity,
      BigDecimal totalAmount) {
    this.id = id;
    this.userId = userId;
    this.productId = productId;
    this.productName = productName;
    this.productPrice = productPrice;
    this.quantity = quantity;
    this.totalAmount = totalAmount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public BigDecimal getProductPrice() {
    return productPrice;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

}
