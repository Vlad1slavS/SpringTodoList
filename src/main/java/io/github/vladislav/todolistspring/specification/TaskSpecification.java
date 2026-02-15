package io.github.vladislav.todolistspring.specification;

import io.github.vladislav.todolistspring.entity.Task;
import io.github.vladislav.todolistspring.enums.TaskStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author Степанов Владислав
 */
@UtilityClass
public class TaskSpecification {
    /**
     * Добавляет фильтр по статусу в Specification
     * @param status - статус задачи
     * @return - Specification с фильтром по статусу
     */
    public Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}
