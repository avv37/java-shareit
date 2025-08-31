package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
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
public class BookingDtoJsonTest {
    private final JacksonTester<BookingResponseDto> jsonResponse;

    @Test
    void testBookingResponseDto() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto(
                1L,
                LocalDateTime.of(2025, Month.AUGUST, 30, 12, 0),
                LocalDateTime.of(2025, Month.AUGUST, 30, 12, 30),
                Status.WAITING,
                new ItemResponseDto(1L,
                        "item name",
                        "item description",
                        true, null, null,
                        List.of(
                                new CommentResponseDto(5L, "good", "user1",
                                        LocalDateTime.of(2025, Month.AUGUST, 30, 13, 0)),
                                new CommentResponseDto(9L, "not bad", "user2",
                                        LocalDateTime.of(2025, Month.AUGUST, 31, 11, 30))
                        )
                ),
                new UserResponseDto(1L, "Karlson", "Karlson@mail.com")
        );

        JsonContent<BookingResponseDto> result = jsonResponse.write(bookingResponseDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-08-30T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-08-30T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.toString());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathArrayValue("$.item.comments").hasSize(2);
        assertThat(result).extractingJsonPathStringValue("$.item.comments.[0].text").isEqualTo("good");
        assertThat(result).extractingJsonPathStringValue("$.item.comments.[0].authorName").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.item.comments.[1].text").isEqualTo("not bad");
        assertThat(result).extractingJsonPathStringValue("$.item.comments.[1].authorName").isEqualTo("user2");
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Karlson");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("Karlson@mail.com");

    }

}
