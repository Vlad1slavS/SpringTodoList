package io.github.vladislav.todolistspring.exception;

import io.github.vladislav.todolistspring.dto.ErrorDto;
import io.github.vladislav.todolistspring.enums.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(EntityNotFoundException e) {
        log.warn("Ресурс не найден: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .message(e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDto.builder()
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String errorMessage = "Неверный формат входных данных";

        if (ex.getCause() instanceof InvalidFormatException invalidFormatEx) {
            Class<?> targetType = invalidFormatEx.getTargetType();
            Object value = invalidFormatEx.getValue();

            if (targetType != null && targetType.isEnum()) {
                String allowedValues = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                errorMessage = String.format(
                        "Неверное значение '%s'. Допустимые значения: %s",
                        value,
                        allowedValues
                );
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .message(errorMessage)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleEnumConversion(
            MethodArgumentTypeMismatchException ex
    ) {
        String errorMessage = "Неверный формат входных данных";

        if (ex.getRequiredType() == TaskStatus.class) {
            Object value = ex.getValue();
            String allowedValues = Arrays.stream(TaskStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            errorMessage = String.format(
                    "Неверное значение '%s'. Допустимые значения: %s",
                    value,
                    allowedValues
            );

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .message(errorMessage)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

}
