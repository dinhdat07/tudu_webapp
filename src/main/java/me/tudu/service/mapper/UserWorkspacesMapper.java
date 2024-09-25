package me.tudu.service.mapper;

import me.tudu.domain.User;
import me.tudu.domain.UserWorkspaces;
import me.tudu.domain.Workspace;
import me.tudu.service.dto.UserDTO;
import me.tudu.service.dto.UserWorkspacesDTO;
import me.tudu.service.dto.WorkspaceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserWorkspaces} and its DTO {@link UserWorkspacesDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserWorkspacesMapper extends EntityMapper<UserWorkspacesDTO, UserWorkspaces> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "workspace", source = "workspace", qualifiedByName = "workspaceId")
    UserWorkspacesDTO toDto(UserWorkspaces s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("workspaceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WorkspaceDTO toDtoWorkspaceId(Workspace workspace);
}
