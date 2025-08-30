package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exception.CommentValidateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidateException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
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
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final UserCreateDto userCreateDto = new UserCreateDto(null, "Karlson", "Karlson@mail.com");
    private final UserCreateDto userCreateDto1 = new UserCreateDto(null, "Fille", "Fille@mail.com");
    private final UserCreateDto userCreateDto2 = new UserCreateDto(null, "Rulle", "Rulle@mail.com");
    private final ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto("descr1");
    private final ItemRequestCreateDto itemRequestCreateDto2 = new ItemRequestCreateDto("descr2");


    @Test
    void shouldCreateItemTest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("name1", "descr1", true, 1L, 1L);
        // Нет такого пользователя
        assertThrows(UserNotFoundException.class, () -> itemService.create(itemCreateDto));
        // создали пользователя
        UserResponseDto userResponseDto = userService.create(userCreateDto);
        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name("name1")
                .description("descr1")
                .available(true)
                .ownerId(userResponseDto.getId())
                .requestId(1L)
                .build();
        // нет такого запроса
        assertThrows(ItemRequestNotFoundException.class, () -> itemService.create(itemCreateDto1));

        // создали запрос
        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequestCreateDto1, userResponseDto.getId());
        // создали вещь
        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name("name1")
                .description("descr1")
                .available(true)
                .ownerId(userResponseDto.getId())
                .requestId(itemRequestDto.getId())
                .build();
        ItemResponseDto itemResponseDto = itemService.create(itemCreateDto2);

        assertThat(itemResponseDto.getId()).isNotNull();
        assertThat(itemResponseDto.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(itemResponseDto.getDescription()).isEqualTo(itemCreateDto.getDescription());
        assertThat(itemResponseDto.getAvailable()).isEqualTo(itemCreateDto.getAvailable());

    }

    @Test
    void shouldUpdateItemTest() {
        // создали пользователей
        UserResponseDto userResponseDto1 = userService.create(userCreateDto1);
        UserResponseDto userResponseDto2 = userService.create(userCreateDto2);
        // создали запрос
        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequestCreateDto1, userResponseDto1.getId());

        // создали вещь, владелец 2
        ItemCreateDto createDto = new ItemCreateDto("name1", "descr1", true,
                userResponseDto2.getId(), itemRequestDto.getId());
        ItemResponseDto itemResponseDto = itemService.create(createDto);

        // делаем апдейт с несуществующим id вещи
        ItemUpdateDto itemUpdateDto1 = ItemUpdateDto.builder()
                .id(10L)
                .name("name new")
                .description("description new")
                .available(true)
                .ownerId(userResponseDto1.getId())
                .build();
        assertThrows(ItemNotFoundException.class, () -> itemService.update(itemUpdateDto1));

        // вещь существует, но неправильный владелец
        ItemUpdateDto itemUpdateDto2 = ItemUpdateDto.builder()
                .id(itemResponseDto.getId())
                .name("name new")
                .description("description new")
                .available(true)
                .ownerId(userResponseDto1.getId())
                .build();
        assertThrows(ItemValidateException.class, () -> itemService.update(itemUpdateDto2));

        // апдейтим вещь с правильными параметрами
        ItemUpdateDto itemUpdateDto = itemUpdateDto2;
        itemUpdateDto.setOwnerId(userResponseDto2.getId());
        itemResponseDto = itemService.update(itemUpdateDto);

        assertThat(itemResponseDto.getId()).isNotNull();
        assertThat(itemResponseDto.getName()).isEqualTo("name new");
        assertThat(itemResponseDto.getDescription()).isEqualTo("description new");
        assertThat(itemResponseDto.getAvailable()).isEqualTo(true);
    }

    @Test
    void shouldGetItemByIdTest() {
        // создали пользователей
        UserResponseDto userResponseDto1 = userService.create(userCreateDto1);
        UserResponseDto userResponseDto2 = userService.create(userCreateDto2);

        // создали запрос 1
        ItemRequestDto itemRequestDto1 = itemRequestService.create(itemRequestCreateDto1, userResponseDto1.getId());
        // создали вещь, владелец 1
        ItemCreateDto itemCreateDto1 = new ItemCreateDto("name1", "descr1", true,
                userResponseDto1.getId(), itemRequestDto1.getId());
        ItemResponseDto itemResponseDto1 = itemService.create(itemCreateDto1);

        // создали запрос 2
        ItemRequestDto itemRequestDto2 = itemRequestService.create(itemRequestCreateDto2, userResponseDto2.getId());
        // создали вещь, владелец 2
        ItemCreateDto itemCreateDto2 = new ItemCreateDto("name2", "descr2", true,
                userResponseDto1.getId(), itemRequestDto2.getId());
        ItemResponseDto itemResponseDto2 = itemService.create(itemCreateDto2);

        // создали бронирования
        BookingCreateDto bookingCreateDto1 = new BookingCreateDto(itemResponseDto1.getId(),
                LocalDateTime.now().minusMinutes(50L), LocalDateTime.now().minusMinutes(40L));
        BookingResponseDto bookingResponseDto1 = bookingService.create(bookingCreateDto1, userResponseDto1.getId());
        BookingCreateDto bookingCreateDto2 = new BookingCreateDto(itemResponseDto2.getId(),
                LocalDateTime.now().minusMinutes(40L), LocalDateTime.now().minusMinutes(30L));
        BookingResponseDto bookingResponseDto2 = bookingService.create(bookingCreateDto2, userResponseDto2.getId());

        // владелец получает с букингом
        ItemResponseDto itemResponseDto10 = itemService.getItemById(itemResponseDto1.getId(), userResponseDto1.getId());
        assertThat(itemResponseDto10.getId()).isNotNull();
        assertThat(itemResponseDto10.getName()).isEqualTo("name1");
        assertThat(itemResponseDto10.getDescription()).isEqualTo("descr1");
        assertThat(itemResponseDto10.getAvailable()).isEqualTo(true);
        assertThat(itemResponseDto10.getLastBooking()).isNotNull();
        assertThat(itemResponseDto10.getNextBooking()).isNull();

        // не владелец получает без букинга
        ItemResponseDto itemResponseDto20 = itemService.getItemById(itemResponseDto1.getId(), userResponseDto2.getId());
        assertThat(itemResponseDto20.getId()).isNotNull();
        assertThat(itemResponseDto20.getName()).isEqualTo("name1");
        assertThat(itemResponseDto20.getDescription()).isEqualTo("descr1");
        assertThat(itemResponseDto20.getAvailable()).isEqualTo(true);
        assertThat(itemResponseDto20.getLastBooking()).isNull();
        assertThat(itemResponseDto20.getNextBooking()).isNull();

    }

    @Test
    void shouldGetItemsByOwnerTest() {
        // создали пользователя
        UserResponseDto userResponseDto = userService.create(userCreateDto);

        // создали запрос 1
        ItemRequestDto itemRequestDto1 = itemRequestService.create(itemRequestCreateDto1, userResponseDto.getId());
        // создали вещь 1
        ItemCreateDto itemCreateDto1 = new ItemCreateDto("name1", "descr1", true,
                userResponseDto.getId(), itemRequestDto1.getId());
        ItemResponseDto itemResponseDto1 = itemService.create(itemCreateDto1);

        // создали запрос 2
        ItemRequestDto itemRequestDto2 = itemRequestService.create(itemRequestCreateDto2, userResponseDto.getId());
        // создали вещь 2
        ItemCreateDto itemCreateDto2 = new ItemCreateDto("name2", "descr2", true,
                userResponseDto.getId(), itemRequestDto2.getId());
        ItemResponseDto itemResponseDto2 = itemService.create(itemCreateDto2);

        // создали бронирования
        BookingCreateDto bookingCreateDto1 = new BookingCreateDto(itemResponseDto1.getId(),
                LocalDateTime.now().minusMinutes(50L), LocalDateTime.now().minusMinutes(40L));
        BookingResponseDto bookingResponseDto1 = bookingService.create(bookingCreateDto1, userResponseDto.getId());
        BookingCreateDto bookingCreateDto2 = new BookingCreateDto(itemResponseDto2.getId(),
                LocalDateTime.now().minusMinutes(40L), LocalDateTime.now().minusMinutes(30L));
        BookingResponseDto bookingResponseDto2 = bookingService.create(bookingCreateDto2, userResponseDto.getId());

        // Получили список по владельцу
        List<ItemResponseDto> itemResponseDtoList = itemService.getItemsByOwner(userResponseDto.getId());

        assertThat(itemResponseDtoList).isNotNull();
        assertThat(itemResponseDtoList.size()).isEqualTo(2);

        ItemResponseDto itemResponseDto = itemResponseDtoList.get(0);

        assertThat(itemResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("name", "name1")
                .hasFieldOrPropertyWithValue("description", "descr1");
        assertThat(itemResponseDto.getAvailable()).isEqualTo(true);
        assertThat(itemResponseDto.getLastBooking()).isNotNull();
        assertThat(itemResponseDto.getNextBooking()).isNull();

        itemResponseDto = itemResponseDtoList.get(1);

        assertThat(itemResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("name", "name2")
                .hasFieldOrPropertyWithValue("description", "descr2");
        assertThat(itemResponseDto.getAvailable()).isEqualTo(true);
        assertThat(itemResponseDto.getLastBooking()).isNotNull();
        assertThat(itemResponseDto.getNextBooking()).isNull();
    }

    @Test
    void shouldSearchItemsByTextTest() {
        // создали пользователя
        UserResponseDto userResponseDto = userService.create(userCreateDto);

        // создали запрос 1
        ItemRequestDto itemRequestDto1 = itemRequestService.create(itemRequestCreateDto1, userResponseDto.getId());
        // создали вещь 1
        ItemCreateDto itemCreateDto1 = new ItemCreateDto("name1", "descr1", true,
                userResponseDto.getId(), itemRequestDto1.getId());
        ItemResponseDto itemResponseDto1 = itemService.create(itemCreateDto1);

        // создали запрос 2
        ItemRequestDto itemRequestDto2 = itemRequestService.create(itemRequestCreateDto2, userResponseDto.getId());
        // создали вещь 2
        ItemCreateDto itemCreateDto2 = new ItemCreateDto("name2", "descr2", true,
                userResponseDto.getId(), itemRequestDto2.getId());
        ItemResponseDto itemResponseDto2 = itemService.create(itemCreateDto2);

        // Получили список по вхождению строки
        List<ItemResponseShortDto> itemResponseDtoList = itemService.searchItemsByText("des", userResponseDto.getId());

        assertThat(itemResponseDtoList).isNotNull();
        assertThat(itemResponseDtoList.size()).isEqualTo(2);

        ItemResponseShortDto itemResponseDto = itemResponseDtoList.get(0);

        assertThat(itemResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("name", "name1")
                .hasFieldOrPropertyWithValue("description", "descr1")
                .hasFieldOrPropertyWithValue("available", true);

        itemResponseDto = itemResponseDtoList.get(1);

        assertThat(itemResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("name", "name2")
                .hasFieldOrPropertyWithValue("description", "descr2")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void shouldAddCommentTest() {
        // создали пользователей, 1-й владелец, 2-й брал в аренду
        UserResponseDto userResponseDto1 = userService.create(userCreateDto1);
        UserResponseDto userResponseDto2 = userService.create(userCreateDto2);

        // создали запрос 1
        ItemRequestDto itemRequestDto1 = itemRequestService.create(itemRequestCreateDto1, userResponseDto1.getId());
        // создали вещь, владелец 1
        ItemCreateDto itemCreateDto1 = new ItemCreateDto("name1", "descr1", true,
                userResponseDto1.getId(), itemRequestDto1.getId());
        ItemResponseDto itemResponseDto1 = itemService.create(itemCreateDto1);

        // создали бронирование
        BookingCreateDto bookingCreateDto1 = new BookingCreateDto(itemResponseDto1.getId(),
                LocalDateTime.now().minusMinutes(50L), LocalDateTime.now().minusMinutes(40L));
        BookingResponseDto bookingResponseDto1 = bookingService.create(bookingCreateDto1, userResponseDto2.getId());

        // комментарий не того, кто брал в аренду
        CommentCreateDto commentCreateDto1 = new CommentCreateDto("good thing", itemResponseDto1.getId(),
                userResponseDto1.getId());
        assertThrows(CommentValidateException.class, () -> itemService.addComment(commentCreateDto1));

        // комментарий арендатора
        CommentCreateDto commentCreateDto2 = new CommentCreateDto("good thing", itemResponseDto1.getId(),
                userResponseDto2.getId());
        CommentResponseDto commentResponseDto = itemService.addComment(commentCreateDto2);

        assertThat(commentResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("text", "good thing")
                .hasFieldOrPropertyWithValue("authorName", userResponseDto2.getName());
        assertThat(commentResponseDto.getCreated()).isNotNull();
    }

}

