package com.example.productapp.server.product.service;

import com.example.productapp.server.product.domain.Product;
import com.example.productapp.server.product.exception.ProductAlreadyExistsException;
import com.example.productapp.server.product.exception.ProductNotFoundException;
import com.example.productapp.server.product.repository.ProductRepository;
import com.example.productapp.server.product.rest.dto.ProductRequestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Product create(Product product) {
        log.debug("Creating product with SKU: {}", product.getSku());

        if (productRepository.existsBySku(product.getSku())) {
            log.debug("Product with SKU {} already exists, throwing exception", product.getSku());
            throw new ProductAlreadyExistsException("Product with SKU " + product.getSku() + " already exists.");
        }

        log.debug("Product created successfully: {}", product);
        return productRepository.save(product);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public Product getById(Long id) {
        log.debug("Fetching product by id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("Product with id {} not found, throwing exception", id);
                    return new ProductNotFoundException(id);
                });

        log.debug("Product fetched successfully: {}", product);
        return product;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public List<Product> getAll() {
        log.debug("Fetching all products");

        List<Product> products = productRepository.findAll();
        log.debug("Total products fetched: {}", products.size());

        return products;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(Long id) {
        log.debug("Deleting product with id: {}", id);

        productRepository.deleteById(id);

        log.debug("Product with id {} deleted successfully", id);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(Long id, ProductRequestResponse requestResponse) {
        log.debug("Updating product with id: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("Product with id {} not found, throwing exception", id);
                    return new ProductNotFoundException(id);
                });

        existingProduct.setName(requestResponse.name());
        existingProduct.setDescription(requestResponse.description());
        existingProduct.setPrice(requestResponse.price());
        existingProduct.setSku(requestResponse.sku());

        productRepository.save(existingProduct);
        log.debug("Product with id {} updated successfully: {}", id, requestResponse);
    }

}
