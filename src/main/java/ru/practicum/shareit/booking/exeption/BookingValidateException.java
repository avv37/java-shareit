package ru.practicum.shareit.booking.exeption;

public class BookingValidateException extends RuntimeException {
    public BookingValidateException(String message) {
        super(message);
    }

}
