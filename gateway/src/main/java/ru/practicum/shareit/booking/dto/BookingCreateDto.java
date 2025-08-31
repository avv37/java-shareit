package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {
    @NotNull(message = "не заполнено поле itemId")
    private Long itemId;
    @NotNull(message = "не заполнено поле start")
    @FutureOrPresent(message = "start должен быть в будущем")
    private LocalDateTime start;
    @NotNull(message = "не заполнено поле end")
    @Future(message = "end должен быть в будущем")
    private LocalDateTime end;
}
