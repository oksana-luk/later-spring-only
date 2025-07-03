package ru.practicum.note;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notes")
public class ItemNoteController {
    private final ItemNoteService itemNoteService;

    @GetMapping(params = "url")
    public List<ItemNoteDto> searchByUrl(@RequestHeader("X-Later-User-Id") long userId,
            @RequestParam(name = "url") String url) {
        return itemNoteService.searchNotesByUrl(url, userId);
    }

    @GetMapping(params = "tag")
    public List<ItemNoteDto> searchByTags(@RequestHeader("X-Later-User-Id") long userId,
                                          @RequestParam(name = "tag") String tag) {
        return itemNoteService.searchNotesByTag(userId, tag);
    }

    @GetMapping
    public List<ItemNoteDto> listAllNotes(@RequestHeader("X-Later-User-Id") long userId,
                                          @RequestParam(name = "from", defaultValue = "0") int from,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemNoteService.listAllItemsWithNotes(userId, from, size);
    }

    @PostMapping
    public ItemNoteDto add(@RequestHeader("X-Later-User-Id") long userId, @RequestBody ItemNoteDto itemNoteDto) {
        return itemNoteService.addNewItemNote(userId, itemNoteDto);
    }
}
