package com.example.productapp.server.product.rest;

import com.example.productapp.server.product.domain.Product;
import com.example.productapp.server.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;


import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Product sampleProduct;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        sampleProduct = new Product(1L,"Test product", "This is a test product", BigDecimal.valueOf(19.99), "TP-001");
    }

    @Test
    void shouldReturnCreatedProduct() throws Exception {
        doReturn(sampleProduct).when(productService).create(any());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value(sampleProduct.getSku()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()));

        verify(productService, times(1)).create(any());
    }


    @Test
    void shouldReturnAllProducts() throws Exception {
        List<Product> products = List.of(sampleProduct);
        doReturn(products).when(productService).getAll();

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value(sampleProduct.getSku()))
                .andExpect(jsonPath("$[0].name").value(sampleProduct.getName()));

        verify(productService, times(1)).getAll();
    }

    @Test
    void shouldReturnProductById() throws Exception {
        doReturn(sampleProduct).when(productService).getById(1L);

        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(sampleProduct.getSku()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()));

        verify(productService, times(1)).getById(1L);
    }

}
