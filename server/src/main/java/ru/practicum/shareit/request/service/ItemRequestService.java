package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestCreateDto createDto, Long userId);

    List<ItemRequestDto> getRequestsByRequestor(Long userId);

    List<ItemRequestDto> getAllRequestsExceptOwn(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
