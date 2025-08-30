package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<Object> create(Long userId, ItemRequestCreateDto createDto) {
        return post("", userId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequestor(Long userId) {
        return get("", userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsExceptOwn(Long userId) {
        return get("/all", userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

}
