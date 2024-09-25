package me.tudu.repository;

import java.util.List;
import me.tudu.domain.UserWorkspaces;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserWorkspaces entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserWorkspacesRepository extends JpaRepository<UserWorkspaces, Long> {
    @Query("select userWorkspaces from UserWorkspaces userWorkspaces where userWorkspaces.user.login = ?#{authentication.name}")
    List<UserWorkspaces> findByUserIsCurrentUser();
}
