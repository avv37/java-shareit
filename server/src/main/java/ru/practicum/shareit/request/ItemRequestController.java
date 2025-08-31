package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(OWNER_ID) Long userId,
                                 @RequestBody ItemRequestCreateDto createDto) {
        return itemRequestService.create(createDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByRequestor(@RequestHeader(OWNER_ID) Long userId) {
        return itemRequestService.getRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsExceptOwn(@RequestHeader(OWNER_ID) Long userId) {
        return itemRequestService.getAllRequestsExceptOwn(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(OWNER_ID) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
