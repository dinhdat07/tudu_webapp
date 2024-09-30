package me.tudu.repository;

import java.util.List;
import java.util.Optional;
import me.tudu.domain.Workspace;
import org.springframework.data.domain.Page;

public interface WorkspaceRepositoryWithBagRelationships {
    Optional<Workspace> fetchBagRelationships(Optional<Workspace> workspace);

    List<Workspace> fetchBagRelationships(List<Workspace> workspaces);

    Page<Workspace> fetchBagRelationships(Page<Workspace> workspaces);
}
