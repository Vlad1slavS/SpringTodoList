package io.github.vladislav.todolistspring.specification;

import io.github.vladislav.todolistspring.entity.Task;
import io.github.vladislav.todolistspring.enums.TaskStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class TaskSpecification {
    public Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}
