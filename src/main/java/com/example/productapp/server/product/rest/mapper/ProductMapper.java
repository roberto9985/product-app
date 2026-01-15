package com.example.productapp.server.product.rest.mapper;

import com.example.productapp.server.product.domain.Product;
import com.example.productapp.server.product.rest.dto.ProductRequestResponse;

import java.util.List;

public class ProductMapper {

    private ProductMapper() { }

    public static ProductRequestResponse productToProductRequestResponse(Product entity) {
        return new ProductRequestResponse(
                entity.getName(),
                entity.getDescription(),
                entity.getPrice()
        );
    }

    public static Product productRequestResponseToProduct(ProductRequestResponse requestResponse) {
        Product entity = new Product();
        entity.setName(requestResponse.name());
        entity.setDescription(requestResponse.description());
        entity.setPrice(requestResponse.price());
        return entity;
    }

    public static List<ProductRequestResponse> productsToProductRequestResponses(List<Product> products) {
        return products.stream()
                .map(ProductMapper::productToProductRequestResponse)
                .toList();
    }
}
