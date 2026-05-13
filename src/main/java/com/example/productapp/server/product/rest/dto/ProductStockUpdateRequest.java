package com.example.productapp.server.product.rest.dto;

public record ProductStockUpdateRequest(Long productId, int quantity) {}
