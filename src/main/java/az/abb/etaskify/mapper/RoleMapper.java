package az.abb.etaskify.mapper;

import az.abb.etaskify.domain.auth.RoleDto;
import az.abb.etaskify.entity.RoleEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto roleToRoleDto(RoleEntity entity);
    RoleEntity roleDtoToRole(RoleDto dto);
    List<RoleDto> rolesToRoleDtos(List<RoleEntity> roleEntities);
}
