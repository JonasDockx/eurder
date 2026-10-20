package com.jonas.eurder.service;

import com.jonas.eurder.api.dto.itemgroup.CreateItemGroupDTO;
import com.jonas.eurder.api.dto.order.CreateOrderDTO;
import com.jonas.eurder.api.dto.order.OrderDTO;
import com.jonas.eurder.api.dto.order.OrderMapper;
import com.jonas.eurder.api.dto.order.OrderReportDTO;
import com.jonas.eurder.domain.*;
import com.jonas.eurder.repository.CustomerRepository;
import com.jonas.eurder.repository.ItemRepository;
import com.jonas.eurder.repository.OrderRepository;
import com.jonas.eurder.service.exception.ForbiddenException;
import com.jonas.eurder.service.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, CustomerRepository customerRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
    }

    public OrderDTO placeOrder(CreateOrderDTO dto) {
        Customer customer = findCustomer(dto.customerId());
        Order order = createOrder(customer, dto.itemGroups());
        logger.info("Order {} placed by customer {}", order.getId(), customer.getId());
        return orderMapper.toDTO(order);
    }

    public OrderDTO reorder(String orderId, String customerId) {
        Order originalOrder = orderRepository.getById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " cannot be found"));
        Customer customer = findCustomer(customerId);
        if (!originalOrder.belongsTo(customer.getId())) {
            throw new ForbiddenException("Customer " + customerId + " cannot reorder order " + orderId);
        }

        List<CreateItemGroupDTO> requestedItemGroups = originalOrder.getItemGroups().stream()
                .map(itemGroup -> new CreateItemGroupDTO(itemGroup.getItemId(), itemGroup.getAmount()))
                .toList();

        Order newOrder = createOrder(customer, requestedItemGroups);
        logger.info("Order {} placed by customer {} as a reorder of order {}",
                newOrder.getId(), customer.getId(), orderId);
        return orderMapper.toDTO(newOrder);
    }

    public OrderReportDTO getOrderReport(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer with id " + customerId + " cannot be found");
        }
        OrderReport report = new OrderReport(orderRepository.getByCustomerId(customerId));
        logger.info("Order report requested for customer {}", customerId);
        return orderMapper.toReportDTO(report);
    }

    private Order createOrder(Customer customer, List<CreateItemGroupDTO> requestedItemGroups) {
        Map<String, Item> itemsById = findItems(requestedItemGroups);
        List<ItemGroup> itemGroups = createItemGroupsAndReduceStock(requestedItemGroups, itemsById, LocalDate.now());
        Order order = new Order(customer.getId(), itemGroups);
        orderRepository.save(order);
        return order;
    }

    private static List<ItemGroup> createItemGroupsAndReduceStock(List<CreateItemGroupDTO> requestedItemGroups, Map<String, Item> itemsById, LocalDate orderDate) {
        List<ItemGroup> itemGroups = new ArrayList<>();
        for (CreateItemGroupDTO requested : requestedItemGroups) {
            Item item = itemsById.get(requested.itemId());
            itemGroups.add(new ItemGroup(item, requested.amount(), orderDate));
            item.decreaseStock(requested.amount());
        }
        return itemGroups;
    }

    private Map<String, Item> findItems(List<CreateItemGroupDTO> requestedItemGroups) {
        Map<String, Item> itemsById = new HashMap<>();
        for (CreateItemGroupDTO requested : requestedItemGroups) {
            Item item = itemRepository.getById(requested.itemId())
                    .orElseThrow(() -> new NotFoundException("Item with id " + requested.itemId() + " could not be found"));
            itemsById.put(item.getId(), item);
        }
        return itemsById;
    }

    private Customer findCustomer(String customerId) {
        return customerRepository.getById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer with id " + customerId + " cannot be found"));
    }
}
