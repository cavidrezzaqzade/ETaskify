package az.abb.etaskify.mapper;

import az.abb.etaskify.domain.OrganizationDto;
import az.abb.etaskify.entity.OrganizationEntity;
import org.mapstruct.Mapper;

/**
 * @author caci
 * @since 20.05.2022
 */

@Mapper(componentModel = "spring")
public interface OrgMapper {
    OrganizationDto orgToOrgDto(OrganizationEntity entity);
    OrganizationEntity orgDtoToOrg(OrganizationDto dto);
}
