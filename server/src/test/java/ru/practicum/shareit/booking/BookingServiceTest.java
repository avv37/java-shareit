package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidateException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;

    private final UserCreateDto userCreateDto = new UserCreateDto(null, "Karlson", "Karlson@mail.com");
    private final UserCreateDto userCreateDto1 = new UserCreateDto(null, "Fille", "Fille@mail.com");
    private final UserCreateDto userCreateDto2 = new UserCreateDto(null, "Rulle", "Rulle@mail.com");
    private final ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto("descr1");
    private final ItemRequestCreateDto itemRequestCreateDto2 = new ItemRequestCreateDto("descr2");
    private final LocalDateTime start = LocalDateTime.now().minusMinutes(50L);
    private final LocalDateTime end = LocalDateTime.now().minusMinutes(30L);
    private UserResponseDto userResponseDto;
    private UserResponseDto userResponseDto1;
    private UserResponseDto userResponseDto2;
    private ItemRequestDto itemRequestDto1;
    private ItemCreateDto itemCreateDto1;
    private ItemResponseDto itemResponseDto1;
    private BookingCreateDto bookingCreateDto1;
    private BookingResponseDto bookingResponseDto1;

    @BeforeEach
    void beforeEach() {
        userResponseDto = userService.create(userCreateDto);
        userResponseDto1 = userService.create(userCreateDto1);
        userResponseDto2 = userService.create(userCreateDto2);
        itemRequestDto1 = itemRequestService.create(itemRequestCreateDto1, userResponseDto1.getId());
        // создали вещь, владелец 1
        itemCreateDto1 = new ItemCreateDto("name1", "descr1", true,
                userResponseDto1.getId(), itemRequestDto1.getId());
        itemResponseDto1 = itemService.create(itemCreateDto1);
        // забронировали
        bookingCreateDto1 = new BookingCreateDto(itemResponseDto1.getId(), start, end);
        bookingResponseDto1 = bookingService.create(bookingCreateDto1, userResponseDto2.getId());
    }

    @Test
    void shouldCreateBookingTest() {
        // 1-й владелец, 2-й брал в аренду

        // создали вещь, владелец 1, недоступна для аренды
        ItemCreateDto itemCreateDtoFalse = new ItemCreateDto("name1", "descr1", false,
                userResponseDto1.getId(), itemRequestDto1.getId());
        ItemResponseDto itemResponseDtoFalse = itemService.create(itemCreateDtoFalse);

        // бронирование недоступной вещи
        BookingCreateDto bookingCreateDtoFalse = new BookingCreateDto(itemResponseDtoFalse.getId(), start, end);

        assertThrows(BookingValidateException.class, () -> bookingService.create(bookingCreateDtoFalse, userResponseDto2.getId()));

        // сделали вещь доступной для аренды
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemResponseDtoFalse.getId(), "name1", "descr1",
                true, userResponseDto1.getId());
        ItemResponseDto itemResponseDto = itemService.update(itemUpdateDto);

        // бронирование доступной вещи
        BookingCreateDto bookingCreateDto = new BookingCreateDto(itemResponseDto.getId(), start, end);

        BookingResponseDto bookingResponseDto = bookingService.create(bookingCreateDto, userResponseDto2.getId());

        assertThat(bookingResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", Status.WAITING);
        assertThat(bookingResponseDto.getItem()).isNotNull();
        assertThat(bookingResponseDto.getBooker()).isNotNull();
    }

    @Test
    void shouldApproveBookingTest() {
        // 1-й владелец, 2-й брал в аренду
        // Одобряем несуществующее бронирование
        assertThrows(BookingNotFoundException.class, () -> bookingService.approve(100L, true,
                userResponseDto1.getId()));
        // Одобряет не владелец
        assertThrows(BookingValidateException.class, () -> bookingService.approve(bookingResponseDto1.getId(),
                true, userResponseDto2.getId()));

        // Правильное одобрение
        BookingResponseDto bookingResponseDto2 = bookingService.approve(bookingResponseDto1.getId(),
                true, userResponseDto1.getId());

        assertThat(bookingResponseDto2).isNotNull()
                .hasFieldOrPropertyWithValue("status", Status.APPROVED);

        // Одобряем бронирование не в статусе WAITING
        assertThrows(BookingValidateException.class, () -> bookingService.approve(bookingResponseDto2.getId(),
                true, userResponseDto1.getId()));

    }

    @Test
    void shouldGetBookingByIdTest() {
        // 1-й владелец, 2-й брал в аренду
        // Ищем несуществующее бронирование
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(100L,
                userResponseDto1.getId()));
        // Ищем бронирование с несуществующим пользователем
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(bookingResponseDto1.getId(),
                100L));
        // Ищем бронирование с посторонним пользователем
        assertThrows(BookingValidateException.class, () -> bookingService.getBookingById(bookingResponseDto1.getId(),
                userResponseDto.getId()));

        BookingResponseDto bookingResponseDto = bookingService.getBookingById(bookingResponseDto1.getId(),
                userResponseDto1.getId());

        assertThat(bookingResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", bookingResponseDto1.getId())
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", Status.WAITING);
        assertThat(bookingResponseDto.getItem()).isNotNull();
        assertThat(bookingResponseDto.getBooker()).isNotNull();
    }

    @Test
    void shouldGetBookingsByBookerAndStateTest() {
        // По пользователю, который не бронировал
        List<BookingResponseDto> bookingResponseDtoList = bookingService.getBookingsByBookerAndState("ALL",
                userResponseDto.getId());
        assertThat(bookingResponseDtoList.size()).isEqualTo(0);

        // С таким статусом нет бронирований
        bookingResponseDtoList = bookingService.getBookingsByBookerAndState("FUTURE", userResponseDto2.getId());
        assertThat(bookingResponseDtoList.size()).isEqualTo(0);

        bookingResponseDtoList = bookingService.getBookingsByBookerAndState("ALL", userResponseDto2.getId());

        assertThat(bookingResponseDtoList.size()).isEqualTo(1);
        BookingResponseDto bookingResponseDto = bookingResponseDtoList.getFirst();
        assertThat(bookingResponseDto).isNotNull();

    }

    @Test
    void shouldGetBookingsByOwnerAndStateTest() {
        // По пользователю, который не владеет ни одной вещью
        assertThrows(BookingValidateException.class, () -> bookingService.getBookingsByOwnerAndState("ALL",
                userResponseDto.getId()));

        // С таким статусом нет бронирований
        List<BookingResponseDto> bookingResponseDtoList = bookingService.getBookingsByOwnerAndState("FUTURE",
                userResponseDto1.getId());
        assertThat(bookingResponseDtoList.size()).isEqualTo(0);

        bookingResponseDtoList = bookingService.getBookingsByOwnerAndState("ALL", userResponseDto1.getId());

        assertThat(bookingResponseDtoList.size()).isEqualTo(1);
        BookingResponseDto bookingResponseDto = bookingResponseDtoList.getFirst();
        assertThat(bookingResponseDto).isNotNull();

    }

}
