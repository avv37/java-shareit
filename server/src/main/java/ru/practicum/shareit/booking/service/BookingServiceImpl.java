package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidateException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingCreateDto bookingDto, Long userId) {
        log.info("create BookingCreateDto = {}, userId = {}", bookingDto, userId);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id = " + itemId + " не найдена"));
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new BookingValidateException("Время начала аренды должно быть меньше времени конца");
        }
        if (!item.getAvailable()) {
            throw new BookingValidateException("Вещь недоступна для аренды");
        }
        Booking booking = bookingRepository.save(BookingMapper.createDtoToBooking(bookingDto, item, booker, Status.WAITING));

        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking,
                ItemMapper.toItemResponseDto(item, null, null, null),
                UserMapper.toUserResponseDto(booker));
        log.info("create BookingResponseDto = {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long bookingId, Boolean approved, Long userId) {
        log.info("approve bookingId = {}, approved = {}, userId = {}", bookingId, approved, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        userRepository.findById(userId)
                .orElseThrow(() -> new BookingValidateException("Пользователь с id = " + userId + " не найден"));
        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new BookingValidateException("Пользователь с id = " + userId
                    + " не является владельцем вещи из брони " + bookingId);
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BookingValidateException("Бронирование с id = " + bookingId + " не в статусе WAITING");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);

        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking,
                ItemMapper.toItemResponseDto(item, null, null, null),
                UserMapper.toUserResponseDto(booking.getBooker()));

        log.info("create BookingResponseDto = {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        log.info("getBookingById bookingId = {}, userId = {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingValidateException("Пользователь с id = " + userId
                    + " не является владельцем вещи или автором бронирования брони " + bookingId);
        }

        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking,
                ItemMapper.toItemResponseDto(booking.getItem(), null, null, null),
                UserMapper.toUserResponseDto(booking.getBooker()));

        log.info("create BookingResponseDto = {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @Override
    public List<BookingResponseDto> getBookingsByBookerAndState(String state, Long userId) {
        log.info("getBookingsByBookerAndState state = {}, userId = {}", state, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
        State stateSt;
        try {
            stateSt = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BookingValidateException("Статус " + state + " не предусмотрен");
        }
        Sort orderByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList = switch (stateSt) {
            case ALL -> bookingRepository.findByBookerId(userId, orderByStartDesc);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId,
                    LocalDateTime.now(), LocalDateTime.now(), orderByStartDesc);
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now(),
                    orderByStartDesc);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now(),
                    orderByStartDesc);
            case WAITING, REJECTED -> bookingRepository.findByBookerIdAndStatus(userId,
                    Status.valueOf(state.toUpperCase()), orderByStartDesc);
        };

        return bookingList.stream()
                .map(booking -> BookingMapper.toBookingResponseDto(
                                booking,
                                ItemMapper.toItemResponseDto(booking.getItem(), null, null, null),
                                UserMapper.toUserResponseDto(booking.getBooker())
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwnerAndState(String state, Long userId) {
        log.info("getBookingsByOwnerAndState state = {}, userId = {}", state, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
        if (itemRepository.findFirstByOwnerId(userId) == null) {
            throw new BookingValidateException("Пользователь " + userId + " не владеет ни одной вещью");
        }
        State stateSt;
        try {
            stateSt = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BookingValidateException("Статус " + state + " не предусмотрен");
        }
        Sort orderByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList = switch (stateSt) {
            case ALL -> bookingRepository.findByItemOwnerId(userId, orderByStartDesc);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(userId,
                    LocalDateTime.now(), LocalDateTime.now(), orderByStartDesc);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(),
                    orderByStartDesc);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(),
                    orderByStartDesc);
            case WAITING, REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(userId,
                    Status.valueOf(state.toUpperCase()), orderByStartDesc);
        };

        return bookingList.stream()
                .map(booking -> BookingMapper.toBookingResponseDto(
                                booking,
                                ItemMapper.toItemResponseDto(booking.getItem(), null, null, null),
                                UserMapper.toUserResponseDto(booking.getBooker())
                        )
                )
                .collect(Collectors.toList());
    }
}
