package io.github.vladislav.todolistspring.service.unit;

import io.github.vladislav.todolistspring.dto.tasks.TaskDto;
import io.github.vladislav.todolistspring.dto.tasks.TaskFilterDto;
import io.github.vladislav.todolistspring.dto.tasks.TaskPatchDto;
import io.github.vladislav.todolistspring.entity.Task;
import io.github.vladislav.todolistspring.enums.TaskStatus;
import io.github.vladislav.todolistspring.mapper.TaskMapper;
import io.github.vladislav.todolistspring.repository.TaskRepository;
import io.github.vladislav.todolistspring.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Test
    void getAllTasksTest_success() {
        List<Task> tasks = generateTasks(4);
        Page<Task> page = new PageImpl<>(tasks);
        Pageable pageable = PageRequest.of(0, 10);
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(null)
                .build();

        when(taskRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        assertEquals(4L, taskService.getAllTasks(filterDto, pageable).getTotalElements());

    }

    @Test
    void getTaskByIdTest_success() {
        UUID id = UUID.randomUUID();
        Task task = Task.builder()
                .id(id)
                .title("Task title")
                .description("description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.ofNullable(task));

        assertEquals(id, taskService.getTaskById(id).getId());
    }

    @Test
    void getTaskByIdTest_TaskNotFount() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenThrow(new EntityNotFoundException("Задача с " + id + " не найдена"));

        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(id));

    }

    @Test
    void createTaskTest_success() {

        LocalDate dueDate = LocalDate.now().plusDays(10);

        TaskDto taskDto = TaskDto.builder()
                .status(TaskStatus.IN_PROGRESS)
                .title("Task title")
                .dueDate(dueDate)
                .build();

        Task mappedTask = Task.builder()
                .title(taskDto.title())
                .status(taskDto.status())
                .dueDate(taskDto.dueDate())
                .build();

        Task savedTask = Task.builder()
                .id(UUID.randomUUID())
                .title(taskDto.title())
                .status(taskDto.status())
                .dueDate(taskDto.dueDate())
                .build();

        when(taskMapper.toEntity(taskDto)).thenReturn(mappedTask);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(taskDto);

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Task title", result.getTitle());
        assertEquals(dueDate, result.getDueDate());

    }

    @Test
    void updateTaskTest_successUpdate() {
        UUID id = UUID.randomUUID();

        TaskPatchDto taskPatchDto = TaskPatchDto.builder()
                .title("Updated title")
                .description("Updated description")
                .build();

        Task existingTask = Task.builder()
                .id(id)
                .title("Old title")
                .description("Old description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(existingTask));

        Task result = taskService.updateTask(taskPatchDto, id);

        assertEquals(id, result.getId());
    }

    @Test
    void updateTaskTest_TaskForUpdateNotFound() {
        UUID id = UUID.randomUUID();

        TaskPatchDto taskPatchDto = TaskPatchDto.builder()
                .title("Updated title")
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(taskPatchDto, id));
    }

    @Test
    void updateTaskStatusTest_success() {
        UUID id = UUID.randomUUID();

        Task existingTask = Task.builder()
                .id(id)
                .title("Task title")
                .description("description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(existingTask));

        Task result = taskService.updateTaskStatus(TaskStatus.IN_PROGRESS, id);

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(id, result.getId());
    }

    @Test
    void updateTaskStatusTest_TaskNotFound() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.updateTaskStatus(TaskStatus.DONE, id));
    }

    @Test
    void deleteTaskTest_success() {
        UUID id = UUID.randomUUID();

        when(taskRepository.existsById(id)).thenReturn(true);

        taskService.deleteTask(id);

        verify(taskRepository).deleteById(id);
    }

    @Test
    void deleteTaskTest_TaskNotFound() {
        UUID id = UUID.randomUUID();

        when(taskRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(id));
    }

    private List<Task> generateTasks(int n) {
        List<Task> res = new ArrayList<>();
        for (int i = 1; i < n + 1; i++) {
            res.add(Task.builder()
                    .id(UUID.randomUUID())
                    .title("Task title" + i)
                    .description("description")
                    .status(TaskStatus.TODO)
                    .build());
        }
        return res;
    }

}
