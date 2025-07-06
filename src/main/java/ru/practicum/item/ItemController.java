package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.AddItemRequest;
import ru.practicum.item.dto.GetItemRequest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ModifyItemRequest;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Later-User-Id") long userId,
                             @RequestParam(name = "state", defaultValue = "unread") String state,
                             @RequestParam(name = "sort", defaultValue = "newest") String sort,
                             @RequestParam(name = "contentType", defaultValue = "all") String contentType,
                             @RequestParam(name = "limit", defaultValue = "10") int limit,
                             @RequestParam(name = "tags", required = false) Set<String> tags) {

        return itemService.getItems(GetItemRequest.of(userId, state, contentType, sort, limit, tags));
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") long userId,
                          @RequestBody AddItemRequest addItemRequest) {
        return itemService.addNewItem(userId, addItemRequest);
    }

    @PatchMapping
    public ItemDto update(@RequestHeader("X-Later-User-Id") long userId,
                          @RequestParam(name = "replaceTags", defaultValue = "false") Boolean replaceTags,
                          @RequestBody ModifyItemRequest request) {
        return itemService.update(userId, request);

    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Later-User-Id") long userId,
                       @PathVariable(name="itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
