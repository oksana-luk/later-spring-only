package ru.practicum.item;

import ru.practicum.item.dto.AddItemRequest;
import ru.practicum.item.dto.GetItemRequest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ModifyItemRequest;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    List<ItemDto> getItems(long userId, List<String> tags);

    ItemDto addNewItem(long userId, AddItemRequest addItemRequest);

    void deleteItem(long userId, long itemId);

    List<ItemDto> getItems(GetItemRequest request);

    ItemDto update(long userId, ModifyItemRequest modifyItemRequest);
}
