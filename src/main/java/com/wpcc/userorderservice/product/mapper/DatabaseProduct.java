package com.wpcc.userorderservice.product.mapper;

import java.math.BigDecimal;

public record DatabaseProduct(Long id, String name, BigDecimal price, Integer stock) {

}
