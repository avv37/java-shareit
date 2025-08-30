package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> create(ItemCreateDto itemDto, Long ownerId) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(ItemUpdateDto itemDto, Long itemId, Long ownerId) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItemsByText(String text, Long ownerId) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", ownerId, parameters);
    }

    public ResponseEntity<Object> addComment(Long itemId, CommentCreateDto commentDto, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}
