package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.item.CreateItemDTO;
import com.jonas.eurder.api.dto.item.ItemDTO;
import com.jonas.eurder.api.dto.item.ItemWithStockLevelDTO;
import com.jonas.eurder.api.dto.item.UpdateItemDTO;
import com.jonas.eurder.domain.Feature;
import com.jonas.eurder.domain.UrgencyLevel;
import com.jonas.eurder.service.ItemService;
import com.jonas.eurder.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/items")
public class ItemController {

    private final ItemService itemService;
    private final SecurityService securityService;

    public ItemController(ItemService itemService, SecurityService securityService) {
        this.itemService = itemService;
        this.securityService = securityService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO createNewItem(@RequestHeader(value = "Authorization", required = false) String authorization,
                                 @Valid @RequestBody CreateItemDTO dto) {
        securityService.validateAuthorization(authorization, Feature.ADD_ITEM);
        return itemService.createItem(dto);
    }

    @PutMapping(path = "{id}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ItemDTO updateItem(@RequestHeader(value = "Authorization", required = false) String authorization,
                              @Valid @RequestBody UpdateItemDTO dto, @PathVariable String id) {
        securityService.validateAuthorization(authorization, Feature.UPDATE_ITEM);
        return itemService.updateItem(id, dto);
    }

    @GetMapping(path = "/stock", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemWithStockLevelDTO> getItemsWithStockLevels(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                               @RequestParam(required = false) UrgencyLevel urgencyLevel) {
        securityService.validateAuthorization(authorization, Feature.VIEW_STOCK_OVERVIEW);
        return itemService.getItemsWithStockLevels(urgencyLevel);
    }
}
