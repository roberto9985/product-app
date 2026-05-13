package com.example.productapp.server.product.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, int requested, int available) {
        super("Not enough stock for product " + productId + ". Requested: " + requested + ", Available: " + available);
    }
}