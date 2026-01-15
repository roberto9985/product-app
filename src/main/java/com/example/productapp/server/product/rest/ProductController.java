package com.example.productapp.server.product.rest;

import com.example.productapp.server.product.rest.dto.ProductRequestResponse;
import com.example.productapp.server.product.rest.mapper.ProductMapper;
import com.example.productapp.server.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductRequestResponse> createProduct(@RequestBody ProductRequestResponse request) {
        log.info("REST request for creating product with SKU: {}", request.sku());

        ProductRequestResponse response =
                ProductMapper.productToProductRequestResponse(productService.create(ProductMapper.productRequestResponseToProduct(request)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductRequestResponse>> getAllProducts() {
        log.info("REST request for getting all products");

        return ResponseEntity.ok(ProductMapper.productsToProductRequestResponses(productService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRequestResponse> getProductById(@PathVariable Long id) {
        log.info("REST request for fetching product with id: {}", id);

        return ResponseEntity.ok(ProductMapper.productToProductRequestResponse(productService.getById(id)));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("REST request to delete product with id: {}", id);

        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestResponse request
    ) {
        log.info("REST request to update product with id: {}", id);

        productService.update(id, request);
        return ResponseEntity.noContent().build();
    }

}
