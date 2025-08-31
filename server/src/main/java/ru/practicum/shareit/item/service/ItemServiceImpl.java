package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exception.CommentValidateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidateException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Transactional
    @Override
    public ItemResponseDto create(ItemCreateDto itemDto) {
        log.info("create ItemCreateDto = {}", itemDto);
        Long ownerId = itemDto.getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + ownerId + " не найден"));
        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = null;
        if (requestId != null) {
            itemRequest = requestRepository.findById(requestId)
                    .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с id = " + requestId + " не найден"));
        }
        Item item = itemRepository.save(ItemMapper.createDtoToItem(itemDto, owner, itemRequest));
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, null, null, null);
        log.info("create complete, ItemResponseDto = {}", itemResponseDto);
        return itemResponseDto;
    }

    @Transactional
    @Override
    public ItemResponseDto update(ItemUpdateDto itemDto) {
        log.info("update ItemUpdateDto {}", itemDto);
        Long ownerId = itemDto.getOwnerId();
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id = " + ownerId + " не найден");
        }
        Long itemId = itemDto.getId();
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с id = " + itemId + " не найден"));
        if (!Objects.equals(oldItem.getOwner().getId(), ownerId)) {
            throw new ItemValidateException("Item с id = " + itemId + " принадлежит пользователю "
                    + oldItem.getOwner().getId() + ", а не " + ownerId);
        }
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        Item item = itemRepository.save(oldItem);
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, null, null, null);
        log.info("update ItemResponseDto {}", itemResponseDto);
        return itemResponseDto;
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        log.info("service getItemById itemId {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с id = " + itemId + " не найден"));
        List<CommentResponseDto> comments = commentsByItemId(itemId);
        boolean isOwner = (item.getOwner().getId().equals(userId));
        LocalDateTime now = LocalDateTime.now();
        Sort orderByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        Sort orderByStartAsc = Sort.by(Sort.Direction.ASC, "start");
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item,
                BookingMapper.toBookingResponseDto(
                        isOwner ? bookingRepository.findFirstByItemIdAndStartBefore(item.getId(),
                                        now, orderByStartDesc)
                                .orElse(new Booking()) : new Booking(),
                        null, null),
                BookingMapper.toBookingResponseDto(
                        isOwner ? bookingRepository.findFirstByItemIdAndStartAfter(item.getId(),
                                        now, orderByStartAsc)
                                .orElse(new Booking()) : new Booking(),
                        null, null),
                comments);
        log.info("service getItemById itemResponseDto = {}", itemResponseDto);
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getItemsByOwner(Long ownerId) {
        log.info("getItemsByOwner ownerId = {}", ownerId);
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id = " + ownerId + " не найден");
        }
        List<Item> itemList = itemRepository.findByOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();
        Sort orderByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        Sort orderByStartAsc = Sort.by(Sort.Direction.ASC, "start");
        return itemList.stream()
                .map(item -> ItemMapper.toItemResponseDto(item,
                        BookingMapper.toBookingResponseDto(bookingRepository
                                .findFirstByItemIdAndStartBefore(item.getId(), now, orderByStartDesc)
                                .orElse(new Booking()), null, null),
                        BookingMapper.toBookingResponseDto(bookingRepository
                                .findFirstByItemIdAndStartAfter(item.getId(), now, orderByStartAsc)
                                .orElse(new Booking()), null, null),
                        commentsByItemId(item.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseShortDto> searchItemsByText(String text, Long ownerId) {
        log.info("searchItemsByText ownerId = {}, text = {}", ownerId, text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository
                .findByAvailableIsTrueAndNameContainingOrDescriptionContainingAllIgnoreCase(text, text);
        return itemList.stream()
                .map(ItemMapper::toItemResponseShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentResponseDto addComment(CommentCreateDto commentDto) {
        log.info("addComment CommentCreateDto {}", commentDto);
        Long authorId = commentDto.getAuthorId();
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + authorId + " не найден"));

        Long itemId = commentDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с id = " + itemId + " не найден"));
        Sort orderByEndtDesc = Sort.by(Sort.Direction.DESC, "end");

        bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(itemId, authorId,
                        LocalDateTime.now(), orderByEndtDesc)
                .orElseThrow(() -> new CommentValidateException("Пользователь с id = " + authorId +
                        " не брал вещь в аренду"));

        Comment comment = CommentMapper.createDtoToComment(commentDto, author, item, LocalDateTime.now());
        comment = commentRepository.save(comment);
        CommentResponseDto commentResponseDto = CommentMapper.toCommentResponseDto(comment);
        log.info("create commentResponseDto {}", commentResponseDto);
        return commentResponseDto;
    }

    private List<CommentResponseDto> commentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

}
