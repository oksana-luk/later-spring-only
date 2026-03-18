package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.config.WebConfig;
import ru.practicum.item.dto.AddItemRequest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ModifyItemRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({ItemController.class, ItemControllerTestConfig.class, WebConfig.class})
public class ItemControllerWithWebContextTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ItemService itemService;
    private MockMvc mvc;
    private ItemDto itemDto;
    private AddItemRequest addItemRequest;
    private ModifyItemRequest modifyItemRequest;

    @Autowired
    ItemControllerWithWebContextTest(ItemService itemService) {
        this.itemService = itemService;
    }

    @BeforeEach
    public void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
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
        headers.add("X-Later-User-Id", "1");

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(addItemRequest))
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
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
