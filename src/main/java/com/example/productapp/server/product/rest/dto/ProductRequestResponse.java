package com.example.productapp.server.product.rest.dto;

import java.math.BigDecimal;

public record ProductRequestResponse(String name, String description, BigDecimal price) {
}
