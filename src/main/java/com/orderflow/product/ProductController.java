package com.orderflow.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.ProductResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
        @Valid @RequestBody CreateProductRequest request
    ){
        return productService.create(request);
    }
}
