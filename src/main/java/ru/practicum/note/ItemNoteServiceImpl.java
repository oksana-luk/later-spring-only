package ru.practicum.note;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.model.Item;
import ru.practicum.item.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemNoteServiceImpl implements ItemNoteService {

    private final ItemNoteRepository itemNoteRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemNoteDto addNewItemNote(long userId, ItemNoteDto itemNoteDto) {
        Item item = itemRepository.findById(itemNoteDto.getItemId()).orElseThrow(() -> new RuntimeException("Not found"));
        ItemNote itemNote = ItemNoteMapper.mapToItemNote(itemNoteDto, item);
        return ItemNoteMapper.mapToItemNoteDto(itemNoteRepository.save(itemNote));
    }

    @Override
    public List<ItemNoteDto> searchNotesByUrl(String url, Long userId) {
        return ItemNoteMapper.mapToItemNoteDto(itemNoteRepository.findAllByItemUrlContainingAndItemUserId(url, userId));
    }

    @Override
    public List<ItemNoteDto> searchNotesByTag(long userId, String tag) {
        return ItemNoteMapper.mapToItemNoteDto(itemNoteRepository.findByTag(userId, tag));
    }

    @Override
    public List<ItemNoteDto> listAllItemsWithNotes(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemNoteRepository.findAllByItemUserId(userId, page)
                .map(ItemNoteMapper::mapToItemNoteDto)
                .getContent();
    }
}
