package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)

public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusMinutes(5L);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final String formattedStart = start.format(formatter);
    private final String formattedEnd = end.format(formatter);

    @Test
    void shouldCreateBookingTest() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(1L, start, end, Status.WAITING, null, null);

        when(bookingService.create(any(BookingCreateDto.class), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(responseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(formattedStart)))
                .andExpect(jsonPath("$.end", is(formattedEnd)))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())));
    }

    @Test
    void shouldApproveBookingTest() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(1L, start, end, Status.APPROVED, null, null);

        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(responseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(formattedStart)))
                .andExpect(jsonPath("$.end", is(formattedEnd)))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void shouldGetBookingByIdTest() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(1L, start, end, Status.APPROVED, null, null);

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(responseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(formattedStart)))
                .andExpect(jsonPath("$.end", is(formattedEnd)))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void shouldGetBookingsByBookerAndStateTest() throws Exception {
        BookingResponseDto responseDto1 = new BookingResponseDto(1L, start, end, Status.APPROVED, null, null);
        BookingResponseDto responseDto2 = new BookingResponseDto(2L, start, end, Status.WAITING, null, null);

        when(bookingService.getBookingsByBookerAndState(any(), anyLong()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(Status.APPROVED.toString())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is(Status.WAITING.toString())));
    }

    @Test
    void shouldGetBookingsByOwnerAndStateTest() throws Exception {
        BookingResponseDto responseDto1 = new BookingResponseDto(1L, start, end, Status.APPROVED, null, null);
        BookingResponseDto responseDto2 = new BookingResponseDto(2L, start, end, Status.WAITING, null, null);

        when(bookingService.getBookingsByOwnerAndState(any(), anyLong()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(Status.APPROVED.toString())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is(Status.WAITING.toString())));
    }

}
