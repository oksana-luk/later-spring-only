package ru.practicum.item.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ModifyItemRequest {
    private Long itemId;
    private boolean read;
    private Set<String> tags;
    private boolean replaceUTags;

    public boolean hasTags() {
        return ! (tags == null || tags.isEmpty());
    }
}
