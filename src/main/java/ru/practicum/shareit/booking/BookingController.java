package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody @Valid BookingCreateDto bookingDto,
                                     @RequestHeader(USER_ID) @Min(1) Long userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@Positive @PathVariable Long bookingId,
                                      @RequestParam Boolean approved,
                                      @RequestHeader(USER_ID) @Min(1) Long userId) {
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@Positive @PathVariable Long bookingId,
                                             @RequestHeader(USER_ID) @Min(1) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBookerAndState(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                                @RequestHeader(USER_ID) @Min(1) Long userId) {
        return bookingService.getBookingsByBookerAndState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerAndState(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                               @RequestHeader(USER_ID) @Min(1) Long userId) {
        return bookingService.getBookingsByOwnerAndState(state, userId);
    }

}
