package io.github.vladislav.todolistspring.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorDto(
        String message,
        LocalDateTime timestamp){
}
