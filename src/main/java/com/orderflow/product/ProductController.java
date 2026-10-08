package com.orderflow.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.PageResponse;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.dto.UpdateProductRequest;

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

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable  Long id){
        return productService.getById(id);
    }

    @GetMapping 
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable){
        return productService.getAll(pageable);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, 
        @Valid @RequestBody UpdateProductRequest request){
            return productService.update(id, request);
        }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id){
        productService.deactivate(id);
    }
   
}
