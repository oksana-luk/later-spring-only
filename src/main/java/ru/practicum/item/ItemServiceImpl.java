package ru.practicum.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> items = itemRepository.findByUserId(userId);
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getItems(long userId, Set<String> tags) {
        BooleanExpression byUserId = QItem.item.user.id.eq(userId);
        BooleanExpression byAnyTag = QItem.item.tags.any().in(tags);
        Iterable<Item> foundItems = itemRepository.findAll(byUserId.and(byAnyTag));
        return ItemMapper.mapToItemDto(foundItems);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Not found"));
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, user));
        return ItemMapper.mapToItemDto(item);
    }
}
