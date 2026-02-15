package io.github.vladislav.todolistspring.mapper;

import io.github.vladislav.todolistspring.dto.tasks.TaskDto;
import io.github.vladislav.todolistspring.dto.tasks.TaskPatchDto;
import io.github.vladislav.todolistspring.entity.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    Task toEntity(TaskDto taskDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromDto(TaskPatchDto dto, @MappingTarget Task entity);

}
