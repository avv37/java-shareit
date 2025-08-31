package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    void shouldCreateItemTest() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("item1", "descr1", true, 1L, null);
        ItemResponseDto responseDto = ItemResponseDto.builder()
                .id(1L)
                .name(createDto.getName())
                .description(createDto.getDescription())
                .available(createDto.getAvailable())
                .build();

        when(itemService.create(any(ItemCreateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(responseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item1")))
                .andExpect(jsonPath("$.description", is("descr1")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void shouldUpdateItemTest() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto(1L, "item1", "descr1", true, 1L);
        ItemResponseDto responseDto = new ItemResponseDto(1L, "item11", "descr11", true,
                null, null, null);

        when(itemService.update(any(ItemUpdateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item11")))
                .andExpect(jsonPath("$.description", is("descr11")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void shouldGetItemByIdTest() throws Exception {
        ItemResponseDto responseDto = new ItemResponseDto(1L, "item1", "descr1", true,
                null, null, null);

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item1")))
                .andExpect(jsonPath("$.description", is("descr1")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void shouldGetItemsByOwnerTest() throws Exception {
        ItemResponseDto responseDto1 = new ItemResponseDto(1L, "item1", "descr1", true,
                null, null, null);
        ItemResponseDto responseDto2 = new ItemResponseDto(2L, "item2", "descr2", true,
                null, null, null);

        when(itemService.getItemsByOwner(anyLong())).thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/items", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(responseDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(responseDto1.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(responseDto1.getDescription()), String.class));
    }

    @Test
    void shouldSearchItemsByTextTest() throws Exception {
        ItemResponseShortDto responseDto1 = new ItemResponseShortDto(1L, "item1", "descr 1", true);
        ItemResponseShortDto responseDto2 = new ItemResponseShortDto(2L, "item2", "descr 2", true);

        when(itemService.searchItemsByText(anyString(), anyLong()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "descr")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(responseDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(responseDto1.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(responseDto1.getDescription()), String.class));
    }

    @Test
    void shouldAddCommentTest() throws Exception {
        LocalDateTime fixedTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String formattedTime = fixedTime.format(formatter);

        CommentResponseDto responseDto = new CommentResponseDto(1L, "very good", "Karlson", fixedTime);

        when(itemService.addComment(any(CommentCreateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("comment", "very good")
                        .content(mapper.writeValueAsString(responseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("very good")))
                .andExpect(jsonPath("$.authorName", is("Karlson")))
                .andExpect(jsonPath("$.created", is(formattedTime)));
    }

}
