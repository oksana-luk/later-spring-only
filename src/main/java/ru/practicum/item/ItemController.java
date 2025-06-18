package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Later-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") long userId, @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Later-User-Id") long userId,
                       @PathVariable(name="itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
