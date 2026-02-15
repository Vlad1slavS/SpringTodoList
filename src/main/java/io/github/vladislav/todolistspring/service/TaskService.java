package io.github.vladislav.todolistspring.service;

import io.github.vladislav.todolistspring.dto.tasks.TaskDto;
import io.github.vladislav.todolistspring.dto.tasks.TaskFilterDto;
import io.github.vladislav.todolistspring.dto.tasks.TaskPatchDto;
import io.github.vladislav.todolistspring.entity.Task;
import io.github.vladislav.todolistspring.entity.TaskSpecification;
import io.github.vladislav.todolistspring.enums.TaskStatus;
import io.github.vladislav.todolistspring.mapper.TaskMapper;
import io.github.vladislav.todolistspring.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public Page<Task> getAllTasks(TaskFilterDto filter, Pageable pageable) {
        Specification<Task> spec = Specification.where(TaskSpecification.hasStatus(filter.status()));
        return taskRepository.findAll(spec, pageable);
    }

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Задача с " + id + " не найдена"));
    }

    public Task createTask(TaskDto taskDto) {
        return taskRepository.save(taskMapper.toEntity(taskDto));
    }

    @Transactional
    public Task updateTask(TaskPatchDto taskDto, UUID id) {
        Task optionalTask = taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Задача с " + id + " не найдена"));

        taskMapper.updateTaskFromDto(taskDto, optionalTask);
        return optionalTask;
    }

    @Transactional
    public Task updateTaskStatus(TaskStatus status, UUID id) {
        Task optionalTask = taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Задача с " + id + " не найдена"));

        optionalTask.setStatus(status);
        return optionalTask;
    }

    @Transactional
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Задача с id " + id + " не найдена");
        }
        taskRepository.deleteById(id);
    }

}
