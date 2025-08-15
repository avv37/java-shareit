package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@Service
public interface BookingService {
    BookingResponseDto create(BookingCreateDto bookingDto, Long userId);

    BookingResponseDto approve(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto> getBookingsByBookerAndState(String state, Long userId);

    List<BookingResponseDto> getBookingsByOwnerAndState(String state, Long userId);
}
