package me.tudu.service.mapper;

import me.tudu.domain.Task;
import me.tudu.domain.User;
import me.tudu.domain.UserTasks;
import me.tudu.service.dto.TaskDTO;
import me.tudu.service.dto.UserDTO;
import me.tudu.service.dto.UserTasksDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserTasks} and its DTO {@link UserTasksDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserTasksMapper extends EntityMapper<UserTasksDTO, UserTasks> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    UserTasksDTO toDto(UserTasks s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);
}
