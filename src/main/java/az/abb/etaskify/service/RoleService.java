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

    public ResponseEntity<?> addNewRole(RoleDto role){
        log.info("RoleService/addNewRole method started");
        Map<String, String> map = new HashMap<>();

        if(roleRepository.existsByRoleNameIgnoreCase(role.getRoleName()))
            map.put("roleName", "already exists");
        if(!map.isEmpty()) {
            map.forEach((k, v) -> log.error("RoleService/addNewRole method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.ALREADY_EXIST.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName(role.getRoleName().toUpperCase(Locale.ROOT));
//        RoleEntity roleEntity = roleMapper.roleDtoToRole(role);
        roleRepository.save(roleEntity);

        RoleDto dto = roleMapper.roleToRoleDto(roleEntity);
        log.info("RoleService/addNewRole method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_ADD.getValue(), dto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> getRoles(){
        log.info("RoleService/getRoles method started");
        List<RoleEntity> users = roleRepository.findAll();
        List<RoleDto> roleDtos = roleMapper.rolesToRoleDtos(users);
        log.info("RoleService/getRoles method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_GET.getValue(), roleDtos, null, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteRole(Long roleId){
        log.info("RoleService/deleteRole method started");
        Map<String, String> map = new HashMap<>();

        Optional<RoleEntity> roleEntity = roleRepository.findById(roleId);
        if(roleEntity.isEmpty())
            map.put("roleId", "role does not exist");
        if(!map.isEmpty()){
            map.forEach((k, v) -> log.error("RoleService/deleteRole method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(!roleEntity.get().getUsers().isEmpty())
            map.put("roleId", "foreign key constraint violation");
        if(!map.isEmpty()){
            map.forEach((k, v) -> log.error("RoleService/deleteRole method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        roleRepository.delete(roleEntity.get());
        RoleDto dto = roleMapper.roleToRoleDto(roleEntity.get());
        log.info("RoleService/deleteRole method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_DELETE.getValue(), dto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> updateRole(RoleDto role, Long roleId){
        log.info("RoleService/updateRole method started");
        Map<String, String> map = new HashMap<>();
        Optional<RoleEntity> roleEntity = roleRepository.findById(roleId);

        if(roleEntity.isEmpty())
            map.put("roleId", "role does not exist");
        if(!map.isEmpty()) {
            map.forEach((k, v) -> log.error("RoleService/updateRole method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Optional<RoleEntity> roleByRoleName = roleRepository.findByRoleNameIgnoreCase(role.getRoleName());
        if(roleByRoleName.isPresent())
            if(!Objects.equals(roleByRoleName.get().getId(), roleId) && roleByRoleName.get().getRoleName().equalsIgnoreCase(role.getRoleName()))
                map.put("roleName", "roleName already exists");
        if(!map.isEmpty()) {
            map.forEach((k, v) -> log.error("RoleService/updateRole method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        roleEntity.get().setRoleName(role.getRoleName().toUpperCase(Locale.ROOT));
        roleRepository.save(roleEntity.get());

        RoleDto dto = roleMapper.roleToRoleDto(roleEntity.get());
        log.info("RoleService/updateRole method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_UPDATE.getValue(), dto, null, HttpStatus.OK);
    }
}
