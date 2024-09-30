package me.tudu.repository;

import java.util.List;
import java.util.Optional;
import me.tudu.domain.Task;
import org.springframework.data.domain.Page;

public interface TaskRepositoryWithBagRelationships {
    Optional<Task> fetchBagRelationships(Optional<Task> task);

    List<Task> fetchBagRelationships(List<Task> tasks);

    Page<Task> fetchBagRelationships(Page<Task> tasks);
}
