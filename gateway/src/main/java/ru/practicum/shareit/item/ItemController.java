package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemCreateDto itemDto,
                                         @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("create item: {}, ownerId = {}", itemDto, ownerId);
        return itemClient.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemUpdateDto itemDto,
                                         @Positive @PathVariable Long itemId,
                                         @Positive @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("update item: {}, ownerId = {}, itemId = {}", itemDto, ownerId, itemId);
        return itemClient.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @PathVariable Long itemId,
                                              @Positive @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("controller getItemById: itemId = {}, ownerId = {}", itemId, ownerId);
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("getItemsByOwner: ownerId = {}", ownerId);
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam(required = false, defaultValue = "") String text,
                                                    @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("searchItemsByText: text = {}, ownerId = {}", text, ownerId);
        return itemClient.searchItemsByText(text, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @PathVariable Long itemId,
                                             @RequestBody CommentCreateDto commentDto,
                                             @RequestHeader(OWNER_ID) @Min(1) Long userId) {
        log.info("addComment comment: {}, ownerId = {}, itemId = {}", commentDto, userId, itemId);
        return itemClient.addComment(itemId, commentDto, userId);
    }
}
