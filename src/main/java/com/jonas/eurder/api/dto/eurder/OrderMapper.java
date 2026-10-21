package com.jonas.eurder.api.dto.eurder;

import com.jonas.eurder.api.dto.address.AddressMapper;
import com.jonas.eurder.api.dto.itemgroup.ItemGroupDTO;
import com.jonas.eurder.api.dto.itemgroup.ItemGroupMapper;
import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.ItemGroup;
import com.jonas.eurder.domain.Order;
import com.jonas.eurder.domain.OrderReport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class OrderMapper {

    private final ItemGroupMapper itemGroupMapper;
    private final AddressMapper addressMapper;

    public OrderMapper(ItemGroupMapper itemGroupMapper, AddressMapper addressMapper) {
        this.itemGroupMapper = itemGroupMapper;
        this.addressMapper = addressMapper;
    }

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<ItemGroupDTO> itemGroupDTOS = itemGroupMapper.toDTO(order.getItemGroups());

        return new OrderDTO(
                order.getId(),
                order.getCustomerId(),
                itemGroupDTOS,
                order.getTotalPrice()
        );
    }

    public OrderReportDTO toReportDTO(OrderReport report) {
        return new OrderReportDTO(
                report.getOrders().stream()
                        .map(this::toOrderInReportDTO)
                        .toList(),
                report.getTotalPrice()
        );
    }

    private OrderInReportDTO toOrderInReportDTO(Order order) {
        return new OrderInReportDTO(
                order.getId(),
                order.getItemGroups().stream()
                        .map(this::toItemGroupInReportDTO)
                        .toList(),
                order.getTotalPrice()
        );
    }

    private ItemGroupInReportDTO toItemGroupInReportDTO(ItemGroup itemGroup) {
        return new ItemGroupInReportDTO(
                itemGroup.getItemName(),
                itemGroup.getAmount(),
                itemGroup.getTotalPrice()
        );
    }

    private ShipmentItemGroupDTO toShipmentItemGroupDTO(ItemGroup itemGroup) {
        return new ShipmentItemGroupDTO(
                itemGroup.getItemId(),
                itemGroup.getItemName(),
                itemGroup.getAmount()
        );
    }

    private List<ShipmentItemGroupDTO> toShipmentItemGroupDTO(Collection<ItemGroup> itemGroups) {
        return itemGroups.stream()
                .map(this::toShipmentItemGroupDTO)
                .toList();
    }

    public ShipmentDTO toShipmentDTO(Order order, List<ItemGroup> itemGroups, Address shippingAddress) {
        return new ShipmentDTO(
                order.getId(),
                addressMapper.toAddressDTO(shippingAddress),
                toShipmentItemGroupDTO(itemGroups)
        );
    }
}
