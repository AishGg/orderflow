package com.orderflow.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderResponse;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("api/v1/orders")
public class OrderController {
    
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
        @Valid @RequestBody CreateOrderRequest request
    ){
        return orderService.create(request);
    }

}
