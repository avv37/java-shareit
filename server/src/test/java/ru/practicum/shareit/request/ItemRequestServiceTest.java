package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;

    private final UserCreateDto userCreateDto = new UserCreateDto(null, "Karlson", "Karlson@mail.com");
    private final UserCreateDto userCreateDto1 = new UserCreateDto(null, "Fille", "Fille@mail.com");
    private final UserCreateDto userCreateDto2 = new UserCreateDto(null, "Rulle", "Rulle@mail.com");
    private UserResponseDto userResponseDto;
    private UserResponseDto userResponseDto1;
    private UserResponseDto userResponseDto2;
    private final ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto("description1");
    private final ItemRequestCreateDto itemRequestCreateDto2 = new ItemRequestCreateDto("description2");
    private final ItemRequestCreateDto itemRequestCreateDto10 = new ItemRequestCreateDto("description10");
    private final ItemRequestCreateDto itemRequestCreateDto20 = new ItemRequestCreateDto("description20");
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto10;
    private ItemRequestDto itemRequestDto2;
    private ItemRequestDto itemRequestDto20;
    private ItemCreateDto itemCreateDto1;
    private ItemCreateDto itemCreateDto10;
    private ItemCreateDto itemCreateDto2;
    private ItemCreateDto itemCreateDto20;
    private ItemResponseDto itemResponseDto1;
    private ItemResponseDto itemResponseDto10;
    private ItemResponseDto itemResponseDto2;
    private ItemResponseDto itemResponseDto20;

    @BeforeEach
    void beforeEach() {
        userResponseDto = userService.create(userCreateDto);
        userResponseDto1 = userService.create(userCreateDto1);
        userResponseDto2 = userService.create(userCreateDto2);
        // 2 запроса
        itemRequestDto1 = itemRequestService.create(itemRequestCreateDto1, userResponseDto1.getId());
        itemRequestDto2 = itemRequestService.create(itemRequestCreateDto2, userResponseDto2.getId());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // ещё 2 запроса через секунду, для сортировки по времени
        itemRequestDto10 = itemRequestService.create(itemRequestCreateDto10, userResponseDto1.getId());
        itemRequestDto20 = itemRequestService.create(itemRequestCreateDto20, userResponseDto2.getId());

        // создали вещь, владелец 0, по запросу 1
        itemCreateDto1 = new ItemCreateDto("name1", "description1", true,
                userResponseDto.getId(), itemRequestDto1.getId());
        itemResponseDto1 = itemService.create(itemCreateDto1);
        // создали вещь, владелец 0, по запросу 10
        itemCreateDto10 = new ItemCreateDto("name10", "description10", true,
                userResponseDto.getId(), itemRequestDto10.getId());
        itemResponseDto10 = itemService.create(itemCreateDto10);
        // создали вещь, владелец 0, по запросу 2
        itemCreateDto2 = new ItemCreateDto("name2", "description2", true,
                userResponseDto.getId(), itemRequestDto2.getId());
        itemResponseDto2 = itemService.create(itemCreateDto2);
        // создали вещь, владелец 0, по запросу 20
        itemCreateDto20 = new ItemCreateDto("name20", "description20", true,
                userResponseDto.getId(), itemRequestDto20.getId());
        itemResponseDto20 = itemService.create(itemCreateDto20);
    }

    @Test
    void shouldCreateItemRequestTest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description");
        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequestCreateDto, userResponseDto.getId());
        assertThat(itemRequestDto).isNotNull()
                .hasFieldOrPropertyWithValue("description", "description");
    }

    @Test
    void shouldGetRequestsByRequestorTest() {
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getRequestsByRequestor(userResponseDto1.getId());

        assertThat(itemRequestDtoList.size()).isEqualTo(2);

        ItemRequestDto itemRequestDto = itemRequestDtoList.getFirst();
        assertThat(itemRequestDto).isNotNull()
                .hasFieldOrPropertyWithValue("description", "description10");

        List<ItemDtoForRequest> itemDtoForRequestList = itemRequestDto.getItems();
        assertThat(itemDtoForRequestList.size()).isEqualTo(1);

        itemRequestDto = itemRequestDtoList.getLast();
        assertThat(itemRequestDto).isNotNull()
                .hasFieldOrPropertyWithValue("description", "description1");

        itemDtoForRequestList = itemRequestDto.getItems();
        assertThat(itemDtoForRequestList.size()).isEqualTo(1);
    }

    @Test
    void shouldGetAllRequestsExceptOwnTest() {
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllRequestsExceptOwn(userResponseDto1.getId());

        assertThat(itemRequestDtoList.size()).isEqualTo(2);

        ItemRequestDto itemRequestDto = itemRequestDtoList.getFirst();
        assertThat(itemRequestDto).isNotNull()
                .hasFieldOrPropertyWithValue("description", "description20");

        List<ItemDtoForRequest> itemDtoForRequestList = itemRequestDto.getItems();
        assertThat(itemDtoForRequestList).isNull();

        itemRequestDto = itemRequestDtoList.getLast();
        assertThat(itemRequestDto).isNotNull()
                .hasFieldOrPropertyWithValue("description", "description2");

        itemDtoForRequestList = itemRequestDto.getItems();
        assertThat(itemDtoForRequestList).isNull();
    }

    @Test
    void shouldGetRequestByIdTest() {
        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getRequestById(userResponseDto.getId(),
                100L));

        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(userResponseDto.getId(), itemRequestDto20.getId());

        assertThat(itemRequestDto).isNotNull()
                .hasFieldOrPropertyWithValue("description", "description20");
    }

}
