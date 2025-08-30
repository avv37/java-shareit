package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody BookingCreateDto bookingDto,
                                     @RequestHeader(USER_ID) Long userId) {
        log.info("create booking: bookingDto = {}, userId = {}", bookingDto, userId);
        BookingResponseDto bookingResponseDto = bookingService.create(bookingDto, userId);
        log.info("create booking complete, bookingResponseDto = {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable Long bookingId,
                                      @RequestParam Boolean approved,
                                      @RequestHeader(USER_ID) Long userId) {
        log.info("approve booking: bookingId = {}, approved = {}, userId = {}", bookingId, approved, userId);
        BookingResponseDto bookingResponseDto = bookingService.approve(bookingId, approved, userId);
        log.info("approve booking complete, bookingResponseDto = {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader(USER_ID) Long userId) {
        log.info("getBookingById: bookingId = {}, userId = {}", bookingId, userId);
        BookingResponseDto bookingResponseDto = bookingService.getBookingById(bookingId, userId);
        log.info("getBookingById complete, bookingResponseDto = {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBookerAndState(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                                @RequestHeader(USER_ID) Long userId) {
        log.info("getBookingsByBookerAndState: state = {}, userId = {}", state, userId);
        List<BookingResponseDto> bookingResponseDtoList = bookingService.getBookingsByBookerAndState(state, userId);
        log.info("getBookingsByBookerAndState complete: List<BookingResponseDto> = {}", bookingResponseDtoList);
        return bookingResponseDtoList;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerAndState(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                               @RequestHeader(USER_ID) Long userId) {
        log.info("getBookingsByOwnerAndState: state = {}, userId = {}", state, userId);
        List<BookingResponseDto> bookingResponseDtoList = bookingService.getBookingsByOwnerAndState(state, userId);
        log.info("getBookingsByOwnerAndState complete: List<BookingResponseDto> = {}", bookingResponseDtoList);
        return bookingResponseDtoList;
    }

}
