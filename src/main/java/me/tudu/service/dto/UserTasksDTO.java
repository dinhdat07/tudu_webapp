package me.tudu.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link me.tudu.domain.UserTasks} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserTasksDTO implements Serializable {

    private Long id;

    private String privilege;

    private Instant createdAt;

    private UserDTO user;

    private TaskDTO task;

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

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserTasksDTO)) {
            return false;
        }

        UserTasksDTO userTasksDTO = (UserTasksDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userTasksDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserTasksDTO{" +
            "id=" + getId() +
            ", privilege='" + getPrivilege() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            ", task=" + getTask() +
            "}";
    }
}
