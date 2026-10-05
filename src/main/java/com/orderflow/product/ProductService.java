package com.orderflow.product;

import org.springframework.stereotype.Service;

import com.orderflow.common.exception.ProductAlreadyExistsException;
import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.ProductResponse;

import jakarta.transaction.Transactional;

@Service 
public class ProductService {
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request){
        if(productRepository.existsBySku(request.sku())){
            throw new ProductAlreadyExistsException(request.sku());
        }

        Product product = new Product(
            request.sku(),
            request.name(),
            request.description(),
            request.price()
        );

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(
            savedProduct.getId(),
            savedProduct.getSku(),
            savedProduct.getName(),
            savedProduct.getDescription(),
            savedProduct.getPrice(),
            savedProduct.isActive(),
            savedProduct.getCreatedAt(),
            savedProduct.getUpdatedAt()
        );

    }
}
