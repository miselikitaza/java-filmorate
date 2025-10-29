package ru.yandex.practicum.filmorate.exception.handler;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String error;
    private final String description;
    private final String timestamp;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

}
