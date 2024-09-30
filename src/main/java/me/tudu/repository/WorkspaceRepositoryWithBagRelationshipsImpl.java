package me.tudu.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import me.tudu.domain.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class WorkspaceRepositoryWithBagRelationshipsImpl implements WorkspaceRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String WORKSPACES_PARAMETER = "workspaces";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Workspace> fetchBagRelationships(Optional<Workspace> workspace) {
        return workspace.map(this::fetchUsers);
    }

    @Override
    public Page<Workspace> fetchBagRelationships(Page<Workspace> workspaces) {
        return new PageImpl<>(fetchBagRelationships(workspaces.getContent()), workspaces.getPageable(), workspaces.getTotalElements());
    }

    @Override
    public List<Workspace> fetchBagRelationships(List<Workspace> workspaces) {
        return Optional.of(workspaces).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    Workspace fetchUsers(Workspace result) {
        return entityManager
            .createQuery(
                "select workspace from Workspace workspace left join fetch workspace.users where workspace.id = :id",
                Workspace.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Workspace> fetchUsers(List<Workspace> workspaces) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, workspaces.size()).forEach(index -> order.put(workspaces.get(index).getId(), index));
        List<Workspace> result = entityManager
            .createQuery(
                "select workspace from Workspace workspace left join fetch workspace.users where workspace in :workspaces",
                Workspace.class
            )
            .setParameter(WORKSPACES_PARAMETER, workspaces)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
