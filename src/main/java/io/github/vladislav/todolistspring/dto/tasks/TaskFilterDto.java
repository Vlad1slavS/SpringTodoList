package io.github.vladislav.todolistspring.dto.tasks;

import io.github.vladislav.todolistspring.enums.TaskStatus;
import lombok.Builder;

// DTO для того, чтобы в будущем можно расширить список фильтров
@Builder
public record TaskFilterDto(
        TaskStatus status
) {
}
