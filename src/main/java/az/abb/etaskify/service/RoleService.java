package az.abb.etaskify.service;

import az.abb.etaskify.domain.auth.RoleDto;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.mapper.RoleMapper;
import az.abb.etaskify.repository.RoleRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import az.abb.etaskify.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    private enum Role{
        ADD_METHOD("RoleService/addNewRole"),
        GET_METHOD("RoleService/getRoles"),
        DELETE_METHOD("RoleService/deleteRole"),
        UPDATE_METHOD("RoleService/updateRole");

        private final String methodName;

        Role(String methodName){
            this.methodName = methodName;
        }

        public String getName(){
            return methodName;
        }
    }

    private enum FieldMessage{
        ROLE_ID("roleId", "does not exist"),
        ROLE_FOREIGN("roleId", "foreign key constraint violation"),
        ROLE_NAME("roleName", "already exists");

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

    public ResponseEntity<?> addNewRole(RoleDto role){
        log.info(Role.ADD_METHOD.getName() + " method started");
        Map<String, String> map = new HashMap<>();

        if(roleRepository.existsByRoleNameIgnoreCase(role.getRoleName()))
            return responseValidation(FieldMessage.ROLE_NAME, Role.ADD_METHOD.getName());

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName(role.getRoleName().toUpperCase(Locale.ROOT));
        roleRepository.save(roleEntity);

        RoleDto dto = roleMapper.roleToRoleDto(roleEntity);
        log.info(Role.ADD_METHOD.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_ADD.getValue(), dto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> getRoles(){
        log.info(Role.GET_METHOD.getName() + " method started");
        List<RoleEntity> users = roleRepository.findAll();
        List<RoleDto> roleDtos = roleMapper.rolesToRoleDtos(users);
        log.info(Role.GET_METHOD.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_GET.getValue(), roleDtos, null, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteRole(Long roleId){
        log.info(Role.DELETE_METHOD.getName() + " method started");
        Map<String, String> map = new HashMap<>();

        Optional<RoleEntity> roleEntity = roleRepository.findById(roleId);
        if(roleEntity.isEmpty())
            return responseValidation(FieldMessage.ROLE_ID, Role.DELETE_METHOD.getName());

        if(!roleEntity.get().getUsers().isEmpty())
            return responseValidation(FieldMessage.ROLE_FOREIGN, Role.DELETE_METHOD.getName());

        roleRepository.delete(roleEntity.get());
        RoleDto dto = roleMapper.roleToRoleDto(roleEntity.get());
        log.info(Role.DELETE_METHOD.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_DELETE.getValue(), dto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> updateRole(RoleDto role, Long roleId){
        log.info(Role.UPDATE_METHOD.getName() + " method started");
        Map<String, String> map = new HashMap<>();
        Optional<RoleEntity> roleEntity = roleRepository.findById(roleId);

        if(roleEntity.isEmpty())
            return responseValidation(FieldMessage.ROLE_ID, Role.UPDATE_METHOD.getName());

        Optional<RoleEntity> roleByRoleName = roleRepository.findByRoleNameIgnoreCase(role.getRoleName());
        if(roleByRoleName.isPresent())
            if(!Objects.equals(roleByRoleName.get().getId(), roleId) && roleByRoleName.get().getRoleName().equalsIgnoreCase(role.getRoleName()))
                return responseValidation(FieldMessage.ROLE_NAME, Role.UPDATE_METHOD.getName());

        roleEntity.get().setRoleName(role.getRoleName().toUpperCase(Locale.ROOT));
        roleRepository.save(roleEntity.get());

        RoleDto dto = roleMapper.roleToRoleDto(roleEntity.get());
        log.info(Role.UPDATE_METHOD.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_UPDATE.getValue(), dto, null, HttpStatus.OK);
    }

    private ResponseEntity<?> responseValidation(FieldMessage fm, String methodName){
        Map<String, String> map = new HashMap<>();
        map.put(fm.getField(), fm.getMessage());
        map.forEach((k, v) -> log.error(methodName + " method ended with " + k + " : " +  v + " -> status:" + HttpStatus.UNPROCESSABLE_ENTITY));
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
