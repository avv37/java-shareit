package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserCreateDto userDto);

    UserResponseDto update(Long id, UserUpdateDto userDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getById(Long id);

    boolean existsById(Long id);

    void delete(Long id);
}
