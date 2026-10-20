package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.item.CreateItemDTO;
import com.jonas.eurder.api.dto.item.ItemDTO;
import com.jonas.eurder.api.dto.item.ItemMapper;
import com.jonas.eurder.api.dto.item.UpdateItemDTO;
import com.jonas.eurder.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO createNewItem(@Valid @RequestBody CreateItemDTO dto) {
        return itemService.createItem(dto);
    }

    @PutMapping(path = "{id}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ItemDTO updateItem(@Valid @RequestBody UpdateItemDTO dto, @PathVariable String id) {
        return itemService.updateItem(id, dto);
    }
}
