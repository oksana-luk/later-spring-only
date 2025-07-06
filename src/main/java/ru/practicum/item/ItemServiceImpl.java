package ru.practicum.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.dto.AddItemRequest;
import ru.practicum.item.dto.GetItemRequest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ModifyItemRequest;
import ru.practicum.item.model.Item;
import ru.practicum.item.model.QItem;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UrlMetadataRetriever urlMetadataRetriever;

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> items = itemRepository.findByUserId(userId);
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getItems(long userId, List<String> tags) {
        BooleanExpression byUserId = QItem.item.user.id.eq(userId);
        BooleanExpression byAnyTag = QItem.item.tags.any().in(tags);
        Iterable<Item> foundItems = itemRepository.findAll(byUserId.and(byAnyTag));
        return ItemMapper.mapToItemDto(foundItems);
    }

    @Override
    public List<ItemDto> getItems(GetItemRequest request) {
        // Для поиска ссылок используем QueryDSL чтобы было удобно настраивать разные варианты фильтров
        QItem item = QItem.item;
        // Мы будем анализировать какие фильтры указал пользователь
        // И все нужные условия фильтрации будем собирать в список
        List<BooleanExpression> conditions = new ArrayList<>();
        // Условие, которое будет проверяться всегда - пользователь сделавший запрос
        // должен быть тем же пользователем, что сохранил ссылку
        conditions.add(item.user.id.eq(request.getUserId()));

        GetItemRequest.State state = request.getState();
        // Если пользователь указал, что его интересуют все ссылки, вне зависимости
        // от состояния, тогда пропускаем этот фильтр. В обратном случае анализируем
        // указанное состояние и формируем подходящее условие для запроса
        if (!state.equals(GetItemRequest.State.ALL)) {
            conditions.add(makeStateCondition(state));
        }

        // Если пользователь указал, что его интересуют ссылки вне зависимости
        // от типа их содержимого, то пропускаем фильтра, иначе анализируем
        // указанный тип контента и формируем соответствующее условие
        GetItemRequest.ContentType contentType = request.getContentType();
        if(!contentType.equals(GetItemRequest.ContentType.ALL)) {
            conditions.add(makeContentTypeCondition(contentType));
        }

        // если пользователя интересуют ссылки с конкретными тэгами,
        // то добавляем это условие в запрос
        if (request.hasTags()) {
            conditions.add(item.tags.any().in(request.getTags()));
        }

        // из всех подготовленных условий, составляем единое условие
        BooleanExpression finalExpression = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        // анализируем, какой вариант сортировки выбрал пользователь
        // и какое количество элементов он выбрал для отображения
        Sort sort = makeOrderByClause(request.getSort());
        PageRequest pageRequest = PageRequest.of(0, request.getLimit(), sort);

        // выполняем запрос к базе данных со всеми подготовленными настройками
        // конвертируем результат в DTO и возвращаем контроллеру
        Iterable<Item> items = itemRepository.findAll(finalExpression, pageRequest);
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    @Transactional
    public ItemDto update(long userId, ModifyItemRequest request) {
        //получить item из репозитория
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (item.getUser().getId() != userId) {
            throw new RuntimeException("User is not the owner of link");
        }

        //заполнить unread
        item.setUnread(!request.isRead());

        //заполнить теги в зависимости от флага
        if (request.hasTags()) {
            updateTags(item, request);
        }

        //сохранить в репозиторий
        item = itemRepository.save(item);
        //вернуть dto
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, AddItemRequest addItemRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Not found"));
        UrlMetadataRetriever.UrlMetadata result = urlMetadataRetriever.retrieve(addItemRequest.getUrl());
        Optional<Item> findedItem = itemRepository.findByUserAndResolvedUrl(user, result.getResolvedUrl());
        Item item;
        if (findedItem.isEmpty()) {
            item = itemRepository.save(ItemMapper.mapToItem(addItemRequest.getTags(), user, result));
        } else {
            item = findedItem.get();
            Set<String> newTags = addItemRequest.getTags();
            if (!(newTags == null || newTags.isEmpty())) {
                item.getTags().addAll(newTags);
                itemRepository.save(item);
            }
        }
        return ItemMapper.mapToItemDto(item);
    }

    private BooleanExpression makeStateCondition(GetItemRequest.State state) {
        if (state.equals(GetItemRequest.State.READ)) {
            return QItem.item.unread.isFalse();
        } else {
            return QItem.item.unread.isTrue();
        }
    }

    private BooleanExpression makeContentTypeCondition(GetItemRequest.ContentType contentType) {
        if (contentType.equals(GetItemRequest.ContentType.ARTICLE)) {
            return QItem.item.mimeType.eq("text");
        } else if (contentType.equals(GetItemRequest.ContentType.IMAGE)) {
            return QItem.item.mimeType.eq("image");
        } else {
            return QItem.item.mimeType.eq("video");
        }
    }

    private Sort makeOrderByClause(GetItemRequest.Sort sort) {
        switch (sort) {
            case TITLE: return Sort.by("title").ascending();
            case SITE: return Sort.by("resolvedUrl").ascending();
            case OLDEST: return Sort.by("dateResolved").ascending();
            case NEWEST:
            default: return Sort.by("dateResolved").descending();
        }
    }

    private void updateTags(Item item, ModifyItemRequest request) {
        if (request.isReplaceUTags()) {
            item.setTags(request.getTags());
        } else {
            item.getTags().addAll(request.getTags());
        }
    }
}

