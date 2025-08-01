package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserValidateException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateDto userDto) {
        log.info("Создание пользователя {}, {}", userDto.getName(), userDto.getEmail());
        checkEmailOrThrow(userDto.getEmail());
        User user = userRepository.create(userMapper.createDtoToUser(userDto));
        log.info("Создание пользователя OK, id = {}", user.getId());
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateDto userDto) {
        log.info("Изменение пользователя {}, {}, {}", userDto.getId(), userDto.getName(), userDto.getEmail());
        checkEmailOrThrow(userDto.getEmail());
        User oldUser = userRepository.getById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }
        User user = userRepository.update(oldUser);
        log.info("Изменение пользователя OK");
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Получение всех пользователей");
        return userRepository.getAllUsers().stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getById(Long id) {
        log.info("Получение пользователя по id = {}", id);
        User user = userRepository.getById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление пользователя по id = {}", id);
        userRepository.delete(id);
    }

    private void checkEmailOrThrow(String email) {
        log.info("Проверка неповторяемости email");
        if (userRepository.getAllUsers().stream()
                .anyMatch(user -> user.getEmail().equals(email))) {
            throw new UserValidateException("Пользователь с email { " + email + " } уже существует");
        }
        log.info("email " + email + " OK");
    }

}
