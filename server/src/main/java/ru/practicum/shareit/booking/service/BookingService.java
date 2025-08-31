package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingCreateDto bookingDto, Long userId);

    BookingResponseDto approve(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto> getBookingsByBookerAndState(State state, Long userId);

    List<BookingResponseDto> getBookingsByOwnerAndState(State state, Long userId);
}
