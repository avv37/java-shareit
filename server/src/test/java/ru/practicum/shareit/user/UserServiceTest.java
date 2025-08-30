package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.BookingValidateException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserValidateException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService service;
    private final UserRepository repository;

    @Test
    void shouldCreateUserTest() {
        UserCreateDto userCreateDto = new UserCreateDto(null, "Karlson", "Karlson@mail.com");
        UserResponseDto userResponseDto = service.create(userCreateDto);

        assertThat(userResponseDto.getId()).isNotNull();
        assertThat(userResponseDto.getName()).isEqualTo(userCreateDto.getName());
        assertThat(userResponseDto.getEmail()).isEqualTo(userCreateDto.getEmail());

        UserCreateDto userCreateDtoEmail = new UserCreateDto(null, "Malysh", "Karlson@mail.com");
        assertThrows(UserValidateException.class, () -> service.create(userCreateDtoEmail));

    }

    @Test
    void shouldUpdateUserTest() {
        UserCreateDto userCreateDto = new UserCreateDto(null, "Fille", "Fille@mail.com");
        UserResponseDto userResponseDto = service.create(userCreateDto);

        Long userId = userResponseDto.getId();
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(userResponseDto.getId())
                .name("Rulle")
                .email("Rulle@mail.com")
                .build();

        // несуществующий юзер
        assertThrows(UserNotFoundException.class, () -> service.update(100L, userUpdateDto));

        userResponseDto = service.update(userId, userUpdateDto);

        assertThat(userResponseDto.getId()).isEqualTo(userId);
        assertThat(userResponseDto.getName()).isEqualTo("Rulle");
        assertThat(userResponseDto.getEmail()).isEqualTo("Rulle@mail.com");
        // с уже существующим email
        assertThrows(UserValidateException.class, () -> service.update(userId, userUpdateDto));

    }

    @Test
    void shouldGetUserByIdTest() {
        UserCreateDto userCreateDto = new UserCreateDto(null, "Karlson", "Karlson@mail.com");
        UserResponseDto userResponseDto = service.create(userCreateDto);

        Long userId = userResponseDto.getId();

        userResponseDto = service.getById(userId);
        assertThat(userResponseDto.getId()).isEqualTo(userId);
        assertThat(userResponseDto.getName()).isEqualTo("Karlson");
        assertThat(userResponseDto.getEmail()).isEqualTo("Karlson@mail.com");
    }

    @Test
    void shouldDeleteUserById() {
        UserCreateDto userCreateDto = new UserCreateDto(null, "Karlson", "Karlson@mail.com");
        UserResponseDto userResponseDto = service.create(userCreateDto);

        Long userId = userResponseDto.getId();
        userResponseDto = service.getById(userId);
        assertThat(repository.existsById(userId)).isTrue();

        service.delete(userId);

        assertThat(repository.existsById(userId)).isFalse();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundByIdTest() {
        Long userId = 37L;
        assertThatThrownBy(() -> service.getById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с id = " + userId + " не найден");
    }

    @Test
    void shouldGetAllUsersTest() {
        UserCreateDto user1 = new UserCreateDto(null, "Fille", "Fille@mail.com");
        UserCreateDto user2 = new UserCreateDto(null, "Rulle", "Rulle@mail.com");
        UserResponseDto userResponse1 = service.create(user1);
        UserResponseDto userResponse2 = service.create(user2);

        List<UserResponseDto> userList = service.getAllUsers();

        UserResponseDto user = userList.get(0);
        assertThat(user).isNotNull()
                .hasFieldOrPropertyWithValue("email", "Fille@mail.com")
                .hasFieldOrPropertyWithValue("name", "Fille");
        user = userList.get(1);
        assertThat(user).isNotNull()
                .hasFieldOrPropertyWithValue("email", "Rulle@mail.com")
                .hasFieldOrPropertyWithValue("name", "Rulle");
    }


}
