package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(BookingCreateDto bookingDto, Long userId) {
        return post("", userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(Long bookingId, Boolean approved, Long userId) {
        // PATCH /bookings/{bookingId}?approved={approved}
        return patch("/" + bookingId + "?approved=" + approved, userId, null);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(Long bookingId, Long userId) {
        // GET /bookings/{bookingId}
        return get("/" + bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerAndState(String state, Long userId) {
        // GET /bookings?state={state}
        Map<String, Object> parameters = Map.of("state", state);
        return get("?state={state}", userId, parameters);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerAndState(String state, Long userId) {
        // GET /bookings/owner?state={state}
        Map<String, Object> parameters = Map.of("state", state);
        return get("/owner?state={state}", userId, parameters);
    }
}
