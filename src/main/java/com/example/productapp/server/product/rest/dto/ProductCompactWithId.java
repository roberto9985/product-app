package com.example.productapp.server.product.rest.dto;

import java.math.BigDecimal;

public record ProductCompactWithId(Long id, BigDecimal price) {
}
