package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.CreateOrderDTO;
import com.jonas.eurder.api.dto.OrderDTO;
import com.jonas.eurder.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createNewOrder(@Valid @RequestBody CreateOrderDTO dto) {
        return orderService.placeOrder(dto);
    }
}
