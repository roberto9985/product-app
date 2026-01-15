package com.example.productapp.server.product.rest;

import com.example.productapp.server.product.rest.dto.ProductRequestResponse;
import com.example.productapp.server.product.rest.mapper.ProductMapper;
import com.example.productapp.server.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductRequestResponse> createProduct(@RequestBody ProductRequestResponse request) {
        ProductRequestResponse response =
                ProductMapper.productToProductRequestResponse(productService.create(ProductMapper.productRequestResponseToProduct(request)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductRequestResponse>> getAllProducts() {
        return ResponseEntity.ok(ProductMapper.productsToProductRequestResponses(productService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRequestResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ProductMapper.productToProductRequestResponse(productService.getById(id)));
    }

}
