package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest createDtoToItemRequest(ItemRequestCreateDto itemDto, User requestor, LocalDateTime now) {
        return ItemRequest.builder()
                .description(itemDto.getDescription())
                .requestor(requestor)
                .created(now)
                .build();
    }

    public ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest, List<ItemDtoForRequest> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
