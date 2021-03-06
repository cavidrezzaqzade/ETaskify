package az.abb.etaskify.mapper;

import az.abb.etaskify.domain.AdminUserDto;
import az.abb.etaskify.domain.auth.UserDto;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

//    @Mapping(source = "roles", target = "roles", qualifiedByName = "setRoleToListLong")
    @Mapping(source = "name", target = "firstname")
    @Mapping(source = "surname", target = "lastname")
    List<AdminUserDto> usersToUsersDto(List<UserEntity> userEntities);

//    @Mapping(source = "roles", target = "roles", qualifiedByName = "setRoleToListLong")
    @Mapping(source = "name", target = "firstname")
    @Mapping(source = "surname", target = "lastname")
    AdminUserDto userToUserDto(UserEntity entity);

//    @Named("setRoleToListLong")
//    static List<Long> setRoleToListLong(Set<RoleEntity> roles) {
//        return roles.stream()
//                .map(RoleEntity::getId)
//                .collect(Collectors.toList());
//    }
}
