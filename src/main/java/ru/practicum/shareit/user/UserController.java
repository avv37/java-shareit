package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody UserCreateDto userDto) {
        log.info("create user: {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto update(@Positive @PathVariable Long userId, @Valid @RequestBody UserUpdateDto userDto) {
        log.info("update user: {}, id = {}", userDto, userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@Positive @PathVariable Long userId) {
        log.info("delete user id = {}", userId);
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@Positive @PathVariable Long userId) {
        log.info("get user by id = {}", userId);
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserResponseDto> getList() {
        return userService.getAllUsers();
    }
}
