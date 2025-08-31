package ru.practicum.shareit.user.exception;

public class UserValidateException extends RuntimeException {
    public UserValidateException(String message) {
        super(message);
    }
}
