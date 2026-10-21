package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.eurder.CreateOrderDTO;
import com.jonas.eurder.api.dto.eurder.OrderDTO;
import com.jonas.eurder.api.dto.eurder.ShipmentDTO;
import com.jonas.eurder.domain.Feature;
import com.jonas.eurder.domain.User;
import com.jonas.eurder.service.OrderService;
import com.jonas.eurder.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService orderService;
    private final SecurityService securityService;

    public OrderController(OrderService orderService, SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createNewOrder(@RequestHeader(value = "Authorization", required = false) String authorization,
                                   @Valid @RequestBody CreateOrderDTO dto) {
        User user = securityService.validateAuthorization(authorization, Feature.ORDER_ITEM);
        return orderService.placeOrder(user.getCustomerId(), dto);
    }

    @PostMapping(path = "/{orderId}/reorder", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO reorder(@RequestHeader(value = "Authorization", required = false) String authorization,
                            @PathVariable String orderId) {
        User user = securityService.validateAuthorization(authorization, Feature.REORDER);
        return orderService.reorder(orderId, user.getCustomerId());
    }

    @GetMapping(path = "/shipping-today", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<ShipmentDTO> getTodaysShippingReport(@RequestHeader(value = "Authorization", required = false) String authorization) {
        securityService.validateAuthorization(authorization, Feature.VIEW_SHIPPING_TODAY);
        return orderService.getShippingToday();
    }
}
