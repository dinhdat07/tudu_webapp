package me.tudu.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import me.tudu.domain.Task;
import me.tudu.domain.User;
import me.tudu.domain.Workspace;
import me.tudu.service.dto.TaskDTO;
import me.tudu.service.dto.UserDTO;
import me.tudu.service.dto.WorkspaceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "workspace", source = "workspace", qualifiedByName = "workspaceId")
    @Mapping(target = "users", source = "users", qualifiedByName = "userIdSet")
    TaskDTO toDto(Task s);

    @Mapping(target = "removeUser", ignore = true)
    Task toEntity(TaskDTO taskDTO);

    @Named("workspaceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WorkspaceDTO toDtoWorkspaceId(Workspace workspace);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }
}
