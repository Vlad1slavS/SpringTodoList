package io.github.vladislav.todolistspring.dto.tasks;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TaskPatchDto(
        @NotNull @NotBlank String title,
        @Size(min = 1, max = 120) String description,
        @FutureOrPresent(message = "Дата должна быть сегодня или в будущем") LocalDate dueDate) {
}
