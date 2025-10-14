package com.example.product.controller;

import com.example.product.service.ProductService;
import com.example.product.dto.ProductResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return new ProductResponse();
    }
}
