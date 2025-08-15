package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public final class ItemMapper {

    public Item createDtoToItem(ItemCreateDto itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(itemDto.getRequest())
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item,
                                                    BookingResponseDto lastBook,
                                                    BookingResponseDto nextBook,
                                                    List<CommentResponseDto> comments) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBook)
                .nextBooking(nextBook)
                .comments(comments)
                .build();
    }

}
