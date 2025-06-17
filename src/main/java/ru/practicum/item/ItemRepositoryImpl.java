package ru.practicum.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final Map<Long, List<Item>> repository = new HashMap<>();
    private static long counter = 0L;

    @Override
    public List<Item> findByUserId(long userId) {
        return repository.get(userId);
    }

    @Override
    public Item save(Item item) {
        item.setId(getNext());
        if (repository.containsKey(item.getUserId())) {
            repository.get(item.getUserId()).add(item);

        } else {
            List<Item> listItems = new ArrayList<>();
            listItems.add(item);
            repository.put(item.getUserId(), listItems);
        }
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        List<Item> newList = repository.get(userId).stream()
                .filter(item -> item.getId() != itemId)
                .toList();
        repository.put(userId, newList);
    }

    private long getNext() {
        return ++counter;
    }
}
