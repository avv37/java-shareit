package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemResponseDto> jsonResponse;

    @Test
    void testItemResponseDto() throws Exception {
        ItemResponseDto itemResponseDto = new ItemResponseDto(
                1L,
                "item name",
                "item description",
                true,
                new BookingResponseDto(
                        1L,
                        LocalDateTime.of(2025, Month.AUGUST, 30, 12, 0),
                        LocalDateTime.of(2025, Month.AUGUST, 30, 12, 30),
                        Status.APPROVED,
                        null,
                        new UserResponseDto(1L, "Karlson", "Karlson@mail.com")
                ),
                new BookingResponseDto(
                        1L,
                        LocalDateTime.of(2025, Month.AUGUST, 31, 12, 0),
                        LocalDateTime.of(2025, Month.AUGUST, 31, 12, 30),
                        Status.WAITING,
                        null,
                        new UserResponseDto(1L, "Karlson", "Karlson@mail.com")
                ),
                List.of(
                        new CommentResponseDto(5L, "good", "user1",
                                LocalDateTime.of(2025, Month.AUGUST, 30, 13, 0)),
                        new CommentResponseDto(9L, "not bad", "user2",
                                LocalDateTime.of(2025, Month.AUGUST, 31, 11, 30))
                )
        );

        JsonContent<ItemResponseDto> result = jsonResponse.write(itemResponseDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2025-08-30T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2025-08-30T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo(Status.APPROVED.toString());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.name").isEqualTo("Karlson");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.email").isEqualTo("Karlson@mail.com");

        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2025-08-31T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2025-08-31T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo(Status.WAITING.toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.booker.name").isEqualTo("Karlson");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.booker.email").isEqualTo("Karlson@mail.com");

        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("good");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].authorName").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.comments.[1].text").isEqualTo("not bad");
        assertThat(result).extractingJsonPathStringValue("$.comments.[1].authorName").isEqualTo("user2");

    }

}
