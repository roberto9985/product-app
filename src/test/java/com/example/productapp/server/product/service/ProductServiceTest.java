package com.example.productapp.server.product.service;

import com.example.productapp.server.product.domain.Product;
import com.example.productapp.server.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Random product");
    }

    @Test
    void shouldSaveProduct() {

        doReturn(sampleProduct).when(productRepository).save(sampleProduct);

        Product result = productService.create(sampleProduct);

        assertNotNull(result);
        assertEquals(sampleProduct.getId(), result.getId());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void shouldReturnProduct_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getById(1L);

        assertNotNull(result);
        assertEquals("Random product", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }


    @Test
    void shouldThrowException_whenNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getById(2L);
        });

        assertTrue(exception.getMessage().contains("Product not found with id 2"));
        verify(productRepository, times(1)).findById(2L);
    }


    @Test
    void shouldReturnListOfProducts() {
        List<Product> productList = List.of(sampleProduct);
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> result = productService.getAll();

        assertEquals(1, result.size());
        assertEquals("Random product", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldCallRepositoryDelete() {
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }
}
