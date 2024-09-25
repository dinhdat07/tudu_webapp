package me.tudu.service.mapper;

import me.tudu.domain.Task;
import me.tudu.domain.Workspace;
import me.tudu.service.dto.TaskDTO;
import me.tudu.service.dto.WorkspaceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "workspace", source = "workspace", qualifiedByName = "workspaceId")
    TaskDTO toDto(Task s);

    @Named("workspaceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WorkspaceDTO toDtoWorkspaceId(Workspace workspace);
}
