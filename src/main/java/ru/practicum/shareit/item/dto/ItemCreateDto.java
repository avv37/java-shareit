package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "не заполнено поле name")
    @Size(max = 127, message = "Максимальная длина 127 символов")
    private String name;
    @NotBlank(message = "не заполнено поле description")
    @Size(max = 1000, message = "Максимальная длина 1000 символов")
    private String description;
    @NotNull(message = "не заполнено поле available")
    private Boolean available;
    private Long ownerId;
    private ItemRequest request;
}
