package ru.practicum.item;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemDto {
    private Long id;
    private Long userId;
    private String url;

    public static ItemDto mapToItemDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setUserId(item.getUserId());
        itemDto.setUrl(item.getUrl());
        return itemDto;
    }
}
