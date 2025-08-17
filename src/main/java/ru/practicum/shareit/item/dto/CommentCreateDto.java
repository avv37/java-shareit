package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {
    @NotBlank
    @Size(max = 1000, message = "Максимальная длина 1000 символов")
    private String text;
    @NotNull
    private Long itemId;
    @NotNull
    private Long authorId;
}
