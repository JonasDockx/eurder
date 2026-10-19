package com.jonas.eurder.service;

import com.jonas.eurder.api.dto.CreateItemGroupDTO;
import com.jonas.eurder.api.dto.CreateOrderDTO;
import com.jonas.eurder.api.dto.OrderDTO;
import com.jonas.eurder.api.dto.OrderMapper;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.domain.Item;
import com.jonas.eurder.domain.ItemGroup;
import com.jonas.eurder.domain.Order;
import com.jonas.eurder.repository.CustomerRepository;
import com.jonas.eurder.repository.ItemRepository;
import com.jonas.eurder.repository.OrderRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

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
        Customer customer = customerRepository.getById(dto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer with id " + dto.customerId() + " cannot be found"));

        // Went for a for loop below to be able to show the relevant id, perhaps not necessary
        Map<String, Item> itemsById = new HashMap<>();
        for (CreateItemGroupDTO groupDTO : dto.itemGroups()) {
            Item item = itemRepository.getById(groupDTO.itemId())
                    .orElseThrow(() -> new NotFoundException("Item with id " + groupDTO.itemId() + " cannot be found"));
            itemsById.put(item.getId(), item);
        }

        LocalDate orderDate = LocalDate.now();
        List<ItemGroup> itemGroups = new ArrayList<>();
        for (CreateItemGroupDTO groupDTO : dto.itemGroups()) {
            Item item = itemsById.get(groupDTO.itemId());
            itemGroups.add(new ItemGroup(item, groupDTO.amount(), orderDate));
            item.decreaseStock(groupDTO.amount());
        }

        Order order = new Order(customer.getId(), itemGroups);
        orderRepository.save(order);
        logger.info("Order {} placed by customer {}", order.getId(), customer.getId());
        return orderMapper.toDTO(order);
    }
}
