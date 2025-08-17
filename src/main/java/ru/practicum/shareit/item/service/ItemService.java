package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(ItemCreateDto itemDto);

    ItemResponseDto update(ItemUpdateDto itemDto);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getItemsByOwner(Long userId);

    List<ItemResponseShortDto> searchItemsByText(String text, Long userId);

    CommentResponseDto addComment(CommentCreateDto commentDto);
}
