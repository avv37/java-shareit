package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserResponseDto> jsonResponse;

    @Test
    void testUserDto() throws Exception {
        UserResponseDto userDto = new UserResponseDto(1L, "Karlson", "Karlson@mail.com");

        JsonContent<UserResponseDto> result = jsonResponse.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Karlson");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Karlson@mail.com");
    }

}
