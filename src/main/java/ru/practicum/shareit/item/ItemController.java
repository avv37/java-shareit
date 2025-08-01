package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    public static final String OWNER_ID = "X-Sharer-User-Id";
    public final ItemService itemService;

    @PostMapping
    public ItemResponseDto create(@RequestBody @Valid ItemCreateDto itemDto,
                                  @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("create item: {}, ownerId = {}", itemDto, ownerId);
        return itemService.create(itemDto.toBuilder()
                .ownerId(ownerId)
                .build());
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestBody ItemUpdateDto itemDto,
                                  @Positive @PathVariable Long itemId,
                                  @Positive @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("update item: {}, ownerId = {}, itemId = {}", itemDto, ownerId, itemId);
        return itemService.update(itemDto.toBuilder()
                .ownerId(ownerId)
                .id(itemId)
                .build());
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@Positive @PathVariable Long itemId,
                                       @Positive @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("getItemById: itemId = {}, ownerId = {}", itemId, ownerId);
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("getItemsByOwner: ownerId = {}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItemsByText(@RequestParam(required = false, defaultValue = "") String text,
                                                   @RequestHeader(OWNER_ID) @Min(1) Long ownerId) {
        log.info("getItemById: text = {}, ownerId = {}", text, ownerId);
        return itemService.searchItemsByText(text, ownerId);
    }
}
