package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.eurder.CreateOrderDTO;
import com.jonas.eurder.api.dto.eurder.OrderDTO;
import com.jonas.eurder.api.dto.eurder.ReorderDTO;
import com.jonas.eurder.api.dto.eurder.ShipmentDTO;
import com.jonas.eurder.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping(path = "/{orderId}/reorder", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO reorder(@PathVariable String orderId, @Valid @RequestBody ReorderDTO dto) {
        return orderService.reorder(orderId, dto.customerId());
    }

    @GetMapping(path = "/shipping-today", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<ShipmentDTO> getTodaysShippingReport() {
        return orderService.getShippingToday();
    }
}
