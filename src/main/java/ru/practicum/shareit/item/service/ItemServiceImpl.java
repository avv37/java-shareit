package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemSaveException;
import ru.practicum.shareit.item.exception.ItemValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    public ItemResponseDto create(ItemCreateDto itemDto) {
        log.info("create ItemCreateDto " + itemDto);
        Long ownerId = itemDto.getOwnerId();
        User owner = userRepository.getById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + ownerId + " не найден"));
        Item item = itemRepository.create(itemMapper.createDtoToItem(itemDto, owner))
                .orElseThrow(() -> new ItemSaveException("Item не создан"));
        ItemResponseDto itemResponseDto = itemMapper.toItemResponseDto(item);
        log.info("create ItemResponseDto " + itemResponseDto);
        return itemResponseDto;
    }

    public ItemResponseDto update(ItemUpdateDto itemDto) {
        log.info("update ItemUpdateDto " + itemDto);
        Long ownerId = itemDto.getOwnerId();
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id = " + ownerId + " не найден");
        }
        Long itemId = itemDto.getId();
        Item oldItem = itemRepository.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с id = " + itemId + " не найден"));
        if (!Objects.equals(oldItem.getOwner().getId(), ownerId)) {
            throw new ItemValidateException("Item с id = " + itemId + " принадлежит пользователю "
                    + oldItem.getOwner().getId() + ", а не " + ownerId);
        }
        Item item = itemRepository.update(itemMapper.updateDtoToItem(itemDto, oldItem))
                .orElseThrow(() -> new ItemSaveException("Item не изменен"));
        ItemResponseDto itemResponseDto = itemMapper.toItemResponseDto(item);
        log.info("update ItemResponseDto " + itemResponseDto);
        return itemResponseDto;
    }

    public ItemResponseDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с id = " + itemId + " не найден"));
        return itemMapper.toItemResponseDto(item);
    }

    public List<ItemResponseDto> getItemsByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id = " + ownerId + " не найден");
        }
        List<Item> itemList = itemRepository.getItemsByOwner(ownerId);
        return itemList.stream()
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    public List<ItemResponseDto> searchItemsByText(String text, Long ownerId) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository.searchItemsByText(text);
        return itemList.stream()
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

}
