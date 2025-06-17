package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(ItemDto::mapToItemDto)
                .toList();
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto addNewItem(long userId, ItemCreateDto itemCreateDto) {
        Item item = new Item(userId, itemCreateDto.getUrl());
        return ItemDto.mapToItemDto(itemRepository.save(item));
    }
}
