package io.github.vladislav.todolistspring.dto.tasks;

import io.github.vladislav.todolistspring.entity.Task;
import io.github.vladislav.todolistspring.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO for {@link Task}
 */
@Builder
public record TaskDto(
        @NotNull @NotBlank String title,
        @Size(min = 1, max = 120) String description,
        @NotNull @FutureOrPresent(message = "Дата должна быть сегодня или в будущем") LocalDate dueDate,
        @NotNull TaskStatus status) {
}