package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    User update(User user);

    List<User> getAllUsers();

    Optional<User> getById(Long id);

    boolean existsById(Long id);

    void delete(Long id);
}
