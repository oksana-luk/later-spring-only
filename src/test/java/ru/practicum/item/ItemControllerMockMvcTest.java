package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.item.dto.AddItemRequest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ModifyItemRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerMockMvcTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mvc;

    ItemDto itemDto;
    AddItemRequest addItemRequest;
    ModifyItemRequest modifyItemRequest;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();

        itemDto = ItemDto.builder()
                .id(1L)
                .title("Title")
                .unread(false)
                .normalUrl("https://images.app.goo.gl/9S87i8PKtRCDV1Jc6")
                .resolvedUrl("https://images.app.goo.gl/9S87i8PKtRCDV1Jc6")
                .mimeType("text")
                .hasVideo(false)
                .hasImage(true)
                .dateResolved(LocalDateTime.now().toString())
                .tags(Set.of("Pause", "Peuschen", "Peusla"))
                .build();
        addItemRequest = new AddItemRequest();
        addItemRequest.setUrl("https://images.app.goo.gl/9S87i8PKtRCDV1Jc6");
        addItemRequest.setTags(Set.of("Pause", "Peuschen", "Peusla"));

        modifyItemRequest = ModifyItemRequest.of(1L, true, Set.of("Cloths", "Tshirt"), true);

    }

    @Test
    public void addItem() throws Exception {
        when(itemService.addNewItem(anyLong(), any()))
                .thenReturn(itemDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Later-User-Id", "1");

        mvc.perform(post("/items")
                        .headers(headers)
                        .content(mapper.writeValueAsString(addItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.normalUrl", is(itemDto.getNormalUrl())))
                .andExpect(jsonPath("$.resolvedUrl", is(itemDto.getResolvedUrl())))
                .andExpect(jsonPath("$.mimeType", is(itemDto.getMimeType())))
                .andExpect(jsonPath("$.title", is(itemDto.getTitle())))
                .andExpect(jsonPath("$.hasImage", is(itemDto.isHasImage())))
                .andExpect(jsonPath("$.hasVideo", is(itemDto.isHasVideo())))
                .andExpect(jsonPath("$.unread", is(itemDto.isUnread())))
                .andExpect(jsonPath("$.dateResolved", is(itemDto.getDateResolved())))
                .andExpect(jsonPath("$.tags.length()", is(3)))
                .andExpect(jsonPath("$.tags", containsInAnyOrder("Pause", "Peuschen", "Peusla")));
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.update(anyLong(), any()))
                .thenReturn(itemDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Later-User-Id", "1");

        mvc.perform(patch("/items")
                        .headers(headers)
                        .param("replaceTags", "true")
                        .content(mapper.writeValueAsString(modifyItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.normalUrl", is(itemDto.getNormalUrl())))
                .andExpect(jsonPath("$.resolvedUrl", is(itemDto.getResolvedUrl())))
                .andExpect(jsonPath("$.mimeType", is(itemDto.getMimeType())))
                .andExpect(jsonPath("$.title", is(itemDto.getTitle())))
                .andExpect(jsonPath("$.hasImage", is(itemDto.isHasImage())))
                .andExpect(jsonPath("$.hasVideo", is(itemDto.isHasVideo())))
                .andExpect(jsonPath("$.unread", is(itemDto.isUnread())))
                .andExpect(jsonPath("$.dateResolved", is(itemDto.getDateResolved())))
                .andExpect(jsonPath("$.tags.length()", is(3)))
                .andExpect(jsonPath("$.tags", containsInAnyOrder("Pause", "Peuschen", "Peusla")));
    }
}