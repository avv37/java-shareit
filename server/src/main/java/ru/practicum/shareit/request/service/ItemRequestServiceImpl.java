package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto createDto, Long userId) {
        log.info("create ItemRequestCreateDto = {}, userId = {}", createDto, userId);
        User requestor = getUserOrThrow(userId);
        ItemRequest itemRequest = ItemRequestMapper.createDtoToItemRequest(createDto, requestor, LocalDateTime.now());
        itemRequest = requestRepository.save(itemRequest);
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToItemRequestDto(itemRequest, null);
        log.info("create complete, itemRequestDto = {}", itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getRequestsByRequestor(Long userId) {
        log.info("getRequestsByRequestor, userId = {}", userId);
        getUserOrThrow(userId);
        // Для каждого запроса должны быть указаны описание, дата и время создания, а также список ответов в формате:
        // id вещи, название, id владельца.
        // Запросы должны возвращаться отсортированными от более новых к более старым.
        Sort orderByCreatedDesc = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requestList = requestRepository.findByRequestorId(userId, orderByCreatedDesc);
        List<ItemRequestDto> itemRequestDtoList = requestList.stream()
                .map(itemRequest -> ItemRequestMapper.itemRequestToItemRequestDto(
                        itemRequest,
                        itemRepository.findByRequestId(itemRequest.getId()).stream()
                                .map(ItemMapper::toItemDtoForRequest)
                                .collect(Collectors.toList())
                ))
                .toList();
        log.info("complete getRequestsByRequestor = {}", itemRequestDtoList);
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAllRequestsExceptOwn(Long userId) {
        log.info("getAllRequestsExceptOwn, userId = {}", userId);
        getUserOrThrow(userId);
        Sort orderByCreatedDesc = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requestList = requestRepository.findByRequestorIdNot(userId, orderByCreatedDesc);
        List<ItemRequestDto> itemRequestDtoList = requestList.stream()
                .map(itemRequest -> ItemRequestMapper.itemRequestToItemRequestDto(
                        itemRequest, null
                ))
                .toList();
        log.info("complete getAllRequestsExceptOwn = {}", itemRequestDtoList);
        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("getRequestById, userId = {}, requestId = {}", userId, requestId);
        getUserOrThrow(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с id = " + requestId + " не найден"));
        List<ItemDtoForRequest> itemDtoList = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDtoForRequest)
                .toList();
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToItemRequestDto(itemRequest, itemDtoList);
        log.info("complete getRequestById = {}", itemRequestDto);
        return itemRequestDto;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
