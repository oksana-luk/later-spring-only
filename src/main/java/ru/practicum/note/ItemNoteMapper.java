package ru.practicum.note;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.item.model.Item;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemNoteMapper {
    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss").withZone(ZoneOffset.UTC);

    public static ItemNoteDto mapToItemNoteDto(ItemNote note) {
        if (note == null) {
            return null;
        }
        String dateOfNote = dateTimeFormatter.format(note.getDateOfNote());
        return new ItemNoteDto(
                note.getId(),
                note.getItem().getId(),
                note.getText(),
                dateOfNote,
                note.getItem().getUrl());
    }

    public static ItemNote mapToItemNote(ItemNoteDto noteDto, Item item) {
        ItemNote itemNote = new ItemNote();
        itemNote.setText(noteDto.getText());
        itemNote.setItem(item);
        return itemNote;

    }

    public static List<ItemNoteDto>  mapToItemNoteDto(Iterable<ItemNote> itemNotes) {
        List<ItemNoteDto> dtos = new ArrayList<>();
        for (ItemNote note : itemNotes) {
            dtos.add(mapToItemNoteDto(note));
        }
        return dtos;
    }
}
