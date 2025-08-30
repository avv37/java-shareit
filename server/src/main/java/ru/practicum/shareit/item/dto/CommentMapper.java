package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment createDtoToComment(CommentCreateDto createDto, User author, Item item, LocalDateTime now) {
        return Comment.builder()
                .text(createDto.getText())
                .author(author)
                .item(item)
                .created(now)
                .build();
    }

    public CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
