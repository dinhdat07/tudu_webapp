package me.tudu.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link me.tudu.domain.UserWorkspaces} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserWorkspacesDTO implements Serializable {

    private Long id;

    private String privilege;

    private Instant createdAt;

    private UserDTO user;

    private WorkspaceDTO workspace;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public WorkspaceDTO getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceDTO workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserWorkspacesDTO)) {
            return false;
        }

        UserWorkspacesDTO userWorkspacesDTO = (UserWorkspacesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userWorkspacesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserWorkspacesDTO{" +
            "id=" + getId() +
            ", privilege='" + getPrivilege() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            ", workspace=" + getWorkspace() +
            "}";
    }
}
