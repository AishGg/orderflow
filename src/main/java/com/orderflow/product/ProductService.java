package com.orderflow.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.common.exception.ProductAlreadyExistsException;
import com.orderflow.common.exception.ProductNotFoundException;
import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.PageResponse;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.dto.UpdateProductRequest;


@Service 
public class ProductService {
    
    private final ProductRepository productRepository;

    private ProductResponse toResponse(Product product) {
    return new ProductResponse(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.isActive(),
            product.getCreatedAt(),
            product.getUpdatedAt()
    );
}

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

        return toResponse(savedProduct);

    }

    @Transactional 
    public ProductResponse update(
        Long id,
        UpdateProductRequest request
    ){
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.updateDetails(request.name(), request.description(), request.price());
        return toResponse(product);
    }

    @Transactional 
    public void deactivate(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.deactivate();
    }


    @Transactional(readOnly = true) 
    public ProductResponse getById(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAll(Pageable pageable){
        Page<ProductResponse> page = productRepository.findAll(pageable).map(this::toResponse);
        return PageResponse.from(page);
    }
}
