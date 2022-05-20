package az.abb.etaskify.service;

import az.abb.etaskify.domain.*;
import az.abb.etaskify.domain.auth.UserDto;
import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.mapper.AdminUserMapper;
import az.abb.etaskify.mapper.OrgMapper;
import az.abb.etaskify.mapper.UserMapper;
import az.abb.etaskify.repository.OrganizationRepository;
import az.abb.etaskify.repository.RoleRepository;
import az.abb.etaskify.repository.UserRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author caci
 * @since 20.05.2022
 */

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {
    private final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final OrganizationRepository orgRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrgMapper orgMapper;
    private final AdminUserMapper ;

    public ResponseEntity<?> addNewOrg(OrgUserDto orgUserDto){
        log.info("OrganizationService/addNewOrg method started");
        Map<String, String> map = new HashMap<>();

        if(orgRepository.existsByOrgNameIgnoreCase(orgUserDto.getOrganizationDto().getOrgName()))
            map.put("orgName", "data already exists");
        if(userRepository.existsByUsernameIgnoreCase(orgUserDto.getAdminDto().getUsername()))
            map.put("username", "data already exists");
        if(userRepository.existsByEmailIgnoreCase(orgUserDto.getAdminDto().getEmail()))
            map.put("email", "data already exists");
        if(!map.isEmpty()) {
            map.forEach((k, v) -> log.error("OrganizationService/addNewOrg method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        OrganizationEntity orgEntity = new OrganizationEntity();
        orgEntity.setOrgName(orgUserDto.getOrganizationDto().getOrgName());
        orgEntity.setOrgAddress(orgUserDto.getOrganizationDto().getOrgAddress());
        orgEntity.setOrgPhone(orgUserDto.getOrganizationDto().getOrgPhone());

        orgRepository.save(orgEntity);
        OrganizationDto organizationDto = orgMapper.orgToOrgDto(orgEntity);

        UserEntity user = new UserEntity();
        user.setUsername(orgUserDto.getAdminDto().getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(orgUserDto.getAdminDto().getPassword()));
        user.setEmail(orgUserDto.getAdminDto().getEmail());
        user.setName(orgUserDto.getAdminDto().getFirstname());
        user.setSurname(orgUserDto.getAdminDto().getLastname());
        user.setOrganization(orgEntity);

        Optional<RoleEntity> roleEntity = roleRepository.findByRoleNameIgnoreCase("ADMIN");
        if(roleEntity.isPresent()){
            user.addRole(roleEntity.get());
        }
        else {
            RoleEntity adminRole = new RoleEntity();
            adminRole.setRoleName("ADMIN");
            roleRepository.save(adminRole);
        }

        userRepository.save(user);
        AdminUserDto userDto = .userToUserDto(user);

        OrgUserDto orgUserDtoBack = getOrgUserDto(organizationDto, userDto);
        log.info("OrganizationService/addNewOrg method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_ADD.getValue(), orgUserDtoBack, null, HttpStatus.OK);
    }

    private OrgUserDto getOrgUserDto(OrganizationDto org, AdminUserDto user){
        OrgUserDto orgUserDto = new OrgUserDto();
        orgUserDto.setOrganizationDto(org);
        orgUserDto.setAdminDto(user);
        return orgUserDto;
    }

}
