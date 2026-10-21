package com.orderflow.order;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.product.dto.PageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping
    public PageResponse<OrderResponse> getAllOrders(Pageable pageable) {
        return orderService.getAll(pageable);
    }

     @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancel(id);
    }

}
