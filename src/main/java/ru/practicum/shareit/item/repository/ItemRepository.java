package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item item);

    Optional<Item> getById(Long itemId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> searchItemsByText(String text);
}
