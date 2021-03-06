package az.abb.etaskify.mapper;

import az.abb.etaskify.domain.task.TaskDto;
import az.abb.etaskify.domain.task.TaskProgress;
import az.abb.etaskify.entity.TaskEntity;
import az.abb.etaskify.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "users", target = "users", qualifiedByName = "setUserToListLong")
    @Mapping(source = "progress", target = "progress", qualifiedByName = "enumNameToOrdinal")
    List<TaskDto> tasksToTasksDto(List<TaskEntity> entities);

    @Mapping(source = "users", target = "users", qualifiedByName = "setUserToListLong")
    @Mapping(source = "progress", target = "progress", qualifiedByName = "enumNameToOrdinal")
    TaskDto taskToTaskDto(TaskEntity entity);

    @Named("setUserToListLong")
    static List<Long> setUserToListLong(Set<UserEntity> users) {
        return users.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
    }

    @Named("enumNameToOrdinal")
    static Integer enumNameToOrdinal(String name) {
        return TaskProgress.getOrdinalByName(name);
    }
}
