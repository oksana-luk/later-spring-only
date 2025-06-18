package ru.practicum.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> items = itemRepository.findByUserId(userId);
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
        return ItemMapper.mapToItemDto(item);
    }
}
