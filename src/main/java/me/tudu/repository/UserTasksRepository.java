package me.tudu.repository;

import java.util.List;
import me.tudu.domain.UserTasks;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserTasks entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserTasksRepository extends JpaRepository<UserTasks, Long> {
    @Query("select userTasks from UserTasks userTasks where userTasks.user.login = ?#{authentication.name}")
    List<UserTasks> findByUserIsCurrentUser();
}
