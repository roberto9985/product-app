package com.example.productapp.server.product.service;

import com.example.productapp.server.product.domain.Product;
import com.example.productapp.server.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

}
