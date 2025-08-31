package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> jsonResponse;

    @Test
    void testItemResponseDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "request description",
                LocalDateTime.of(2025, Month.AUGUST, 30, 12, 0),
                List.of(
                        new ItemDtoForRequest(1L, "name", 10L),
                        new ItemDtoForRequest(2L, "name", 20L)
                )
        );


        JsonContent<ItemRequestDto> result = jsonResponse.write(itemRequestDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("request description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-08-30T12:00:00");

        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].ownerId").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.items.[1].name").isEqualTo("name");
        assertThat(result).extractingJsonPathNumberValue("$.items.[1].ownerId").isEqualTo(20);

    }

}
