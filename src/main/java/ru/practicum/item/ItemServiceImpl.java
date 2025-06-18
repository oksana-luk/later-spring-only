package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
        return ItemMapper.mapToItemDto(item);
    }
}
