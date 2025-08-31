package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService requestService;
    @Autowired
    private MockMvc mvc;

    private final LocalDateTime created = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final String formattedCreated = created.format(formatter);

    @Test
    void shouldCreateItemRequestTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(2L, "I want item", created, null);

        when(requestService.create(any(ItemRequestCreateDto.class), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.description", is("I want item")))
                .andExpect(jsonPath("$.created", is(formattedCreated)));
    }

    @Test
    void shouldGetRequestsByRequestorTest() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "I want item 1", created, null);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "I want item 2", created, null);

        when(requestService.getRequestsByRequestor(anyLong())).thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mvc.perform(get("/requests", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(formattedCreated)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].created", is(formattedCreated)))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    void shouldGetAllRequestsExceptOwnTest() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "I want item 1", created, null);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "I want item 2", created, null);

        when(requestService.getAllRequestsExceptOwn(anyLong())).thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mvc.perform(get("/requests/all", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(formattedCreated)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].created", is(formattedCreated)))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    void shouldGetRequestByIdTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "I want item", created, null);

        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("I want item")))
                .andExpect(jsonPath("$.created", is(formattedCreated)));
    }


}
