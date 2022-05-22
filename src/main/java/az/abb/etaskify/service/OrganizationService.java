package az.abb.etaskify.service;

import az.abb.etaskify.domain.*;
import az.abb.etaskify.domain.auth.InRole;
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
    private final AdminUserMapper adminUserMapper;

    private enum Org{
        ADD_METHOD("OrganizationService/addNewOrg");

        private final String methodName;

        Org(String methodName){
            this.methodName = methodName;
        }

        public String getMethodName(){
            return methodName;
        }
    }

    private enum FieldMessage{
        ORG_NAME("orgName", "data already exists"),
        USER_NAME("username", "data already exists"),
        EMAIL("email", "data already exists");

        private final String field;
        private final String message;

        FieldMessage(String field, String message){
            this.message = message;
            this.field = field;
        }

        public String getField(){
            return field;
        }
        public String getMessage(){
            return message;
        }
    }

    public ResponseEntity<?> addNewOrg(OrgUserDto orgUserDto){
        log.info(Org.ADD_METHOD.getMethodName() + " method started");
        Map<String, String> map = new HashMap<>();

        if(orgRepository.existsByOrgNameIgnoreCase(orgUserDto.getOrganizationDto().getOrgName()))
            map.put(FieldMessage.ORG_NAME.getField(), FieldMessage.ORG_NAME.getMessage());
        if(userRepository.existsByUsernameIgnoreCase(orgUserDto.getAdminDto().getUsername()))
            map.put(FieldMessage.USER_NAME.getField(), FieldMessage.USER_NAME.getMessage());
        if(userRepository.existsByEmailIgnoreCase(orgUserDto.getAdminDto().getEmail()))
            map.put(FieldMessage.EMAIL.getField(), FieldMessage.EMAIL.getMessage());
        if(!map.isEmpty()) {
            map.forEach((k, v) -> log.error(Org.ADD_METHOD.getMethodName() + " method ended with " + k + " : " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
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
            adminRole.setRoleName(InRole.ADMIN.getRole());
            roleRepository.save(adminRole);

            RoleEntity userRole = new RoleEntity();
            userRole.setRoleName(InRole.USER.getRole());
            roleRepository.save(userRole);

            user.addRole(adminRole);
//            user.addRole(userRole);
        }

        userRepository.save(user);
        AdminUserDto userDto = adminUserMapper.userToUserDto(user);

        OrgUserDto orgUserDtoBack = getOrgUserDto(organizationDto, userDto);
        log.info(Org.ADD_METHOD.getMethodName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_ADD.getValue(), orgUserDtoBack, null, HttpStatus.OK);
    }

    private OrgUserDto getOrgUserDto(OrganizationDto org, AdminUserDto user){
        OrgUserDto orgUserDto = new OrgUserDto();
        orgUserDto.setOrganizationDto(org);
        orgUserDto.setAdminDto(user);
        return orgUserDto;
    }

}
