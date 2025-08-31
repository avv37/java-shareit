package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void shouldCreateUserTest() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(null, "Karlson", "Karlson@mail.com");
        UserResponseDto userResponseDto = new UserResponseDto(1L, "Karlson", "Karlson@mail.com");

        when(userService.create(any(UserCreateDto.class)))
                .thenReturn(userResponseDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail())));
    }

    @Test
    void shouldUpdateUserTest() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "Karlson", "Karlson@mail.com");
        UserResponseDto userResponseDto = new UserResponseDto(1L, "Karlson Upd", "KarlsonUpd@mail.com");
        when(userService.update(anyLong(), any(UserUpdateDto.class)))
                .thenReturn(userResponseDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail())));
    }

    @Test
    void shouldGetAllUsersTest() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "Fille", "Fille@mail.com");
        UserResponseDto user2 = new UserResponseDto(2L, "Rulle", "Rulle@mail.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user1.getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail()), String.class))
                .andExpect(jsonPath("$[1].id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(user2.getName()), String.class))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail()), String.class));

    }

    @Test
    void shouldGetUserByIdTest() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "Fille", "Fille@mail.com");
        UserResponseDto user2 = new UserResponseDto(2L, "Rulle", "Rulle@mail.com");

        when(userService.getById(2L)).thenReturn(user2);

        mvc.perform(get("/users/2")
                        .header("X-Later-User-Id", user2.getId())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user2.getName())))
                .andExpect(jsonPath("$.email", is(user2.getEmail())));
    }

    @Test
    void shouldDeleteUserByIdTest() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "Fille", "Fille@mail.com");
        UserResponseDto user2 = new UserResponseDto(2L, "Rulle", "Rulle@mail.com");

        doNothing().when(userService).delete(user2.getId());

        mvc.perform(delete("/users/{userId}", user2.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(user2.getId());
    }

    @Test
    public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        Long userId = 37L;

        doThrow(new UserNotFoundException("User not found"))
                .when(userService).delete(userId);

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());
        // 404 если пользователь не найден

        verify(userService, times(1)).delete(userId);
    }

}
