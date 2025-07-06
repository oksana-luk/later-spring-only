package ru.practicum.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Item;
import ru.practicum.user.User;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ItemMapper {
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter
            .ofPattern("yyyy.MM.dd hh:mm:ss")
            .withZone(ZoneOffset.UTC);

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .normalUrl(item.getUrl())
                .resolvedUrl(item.getResolvedUrl())
                .hasImage(item.isHasImage())
                .hasVideo(item.isHasVideo())
                .mimeType(item.getMimeType())
                .unread(item.isUnread())
                .dateResolved(dtFormatter.format(item.getDateResolved()))
                // Нужно скопировать все элементы в новую коллекцию - чтобы запустить механизм ленивой загрузки.
                .tags(new HashSet<>(item.getTags()))
                .build();
    }

    public static Item mapToItem(Set<String> tags, User user, UrlMetadataRetriever.UrlMetadata urlMetadata) {
        Item item = new Item();
        item.setUser(user);
        item.setUrl(urlMetadata.getNormalUrl());
        item.setTags(tags);
        item.setResolvedUrl(urlMetadata.getResolvedUrl());
        item.setMimeType(urlMetadata.getMimeType());
        item.setTitle(urlMetadata.getTitle());
        item.setHasImage(urlMetadata.isHasImage());
        item.setHasVideo(urlMetadata.isHasVideo());
        item.setUnread(false);
        item.setDateResolved(urlMetadata.getDateResolved());
        return item;
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemDto(item));
        }
        return dtos;
    }
}
