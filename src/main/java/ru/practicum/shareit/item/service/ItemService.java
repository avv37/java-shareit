package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Service
public interface ItemService {
    ItemResponseDto create(ItemCreateDto itemDto);

    ItemResponseDto update(ItemUpdateDto itemDto);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getItemsByOwner(Long userId);

    List<ItemResponseDto> searchItemsByText(String text, Long userId);
}
