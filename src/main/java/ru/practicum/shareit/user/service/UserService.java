package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Service
public interface UserService {
    UserResponseDto create(UserCreateDto userDto);

    UserResponseDto update(Long id, UserUpdateDto userDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getById(Long id);

    boolean existsById(Long id);

    void delete(Long id);
}
