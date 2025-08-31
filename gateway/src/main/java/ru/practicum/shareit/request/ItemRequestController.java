package ru.practicum.shareit.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID) Long userId,
                                         @RequestBody ItemRequestCreateDto createDto) {
        return itemRequestClient.create(userId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequestor(@RequestHeader(OWNER_ID) @Min(1) Long userId) {
        return itemRequestClient.getRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsExceptOwn(@RequestHeader(OWNER_ID) @Min(1) Long userId) {
        return itemRequestClient.getAllRequestsExceptOwn(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(OWNER_ID) @Min(1) Long userId,
                                                 @Positive @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
