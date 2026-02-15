package io.github.vladislav.todolistspring.controller;

import io.github.vladislav.todolistspring.dto.tasks.TaskDto;
import io.github.vladislav.todolistspring.dto.tasks.TaskFilterDto;
import io.github.vladislav.todolistspring.dto.PageResponseDto;
import io.github.vladislav.todolistspring.dto.tasks.TaskPatchDto;
import io.github.vladislav.todolistspring.entity.Task;
import io.github.vladislav.todolistspring.enums.TaskStatus;
import io.github.vladislav.todolistspring.mapper.TaskMapper;
import io.github.vladislav.todolistspring.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    public PageResponseDto<TaskDto> getAllTasks(Pageable pageable,
                                                @RequestParam(required = false) TaskStatus status) {
        TaskFilterDto filter = TaskFilterDto.builder()
                .status(status)
                .build();

        Page<Task> res = taskService.getAllTasks(filter, pageable);
        return new PageResponseDto<>(
                res.getContent().stream()
                        .map(taskMapper::toDto)
                        .toList(),
                res.getNumber(),
                res.getSize(),
                res.getTotalElements(),
                res.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public TaskDto getTask(@PathVariable UUID id) {
        return taskMapper.toDto(taskService.getTaskById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/create")
    public TaskDto createTask(@Valid @RequestBody TaskDto taskDto) {
        return taskMapper.toDto(taskService.createTask(taskDto));
    }

    @PatchMapping("/update/{id}")
    public TaskDto updateTask(@Valid @RequestBody TaskPatchDto taskDto, @PathVariable UUID id) {
        return taskMapper.toDto(taskService.updateTask(taskDto, id));
    }

    @PatchMapping("/update/status/{id}")
    public TaskDto updateTaskStatus(@Valid @RequestBody TaskStatus status, @PathVariable UUID id) {
        return taskMapper.toDto(taskService.updateTaskStatus(status, id));
    }

}

