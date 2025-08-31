package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid BookingCreateDto bookingDto,
                                         @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("create booking: bookingDto = {}, userId = {}", bookingDto, userId);
        return bookingClient.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@Positive @PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("approve booking: bookingId = {}, approved = {}, userId = {}", bookingId, approved, userId);
        return bookingClient.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@Positive @PathVariable Long bookingId,
                                                 @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("getBookingById: bookingId = {}, userId = {}", bookingId, userId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerAndState(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                              @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("getBookingsByBookerAndState: state = {}, userId = {}", state, userId);
        State stateSt;
        try {
            stateSt = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Статус " + state + " не предусмотрен");
        }
        return bookingClient.getBookingsByBookerAndState(stateSt, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerAndState(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                             @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("getBookingsByOwnerAndState: state = {}, userId = {}", state, userId);
        State stateSt;
        try {
            stateSt = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Статус " + state + " не предусмотрен");
        }
        return bookingClient.getBookingsByOwnerAndState(stateSt, userId);
    }

}
