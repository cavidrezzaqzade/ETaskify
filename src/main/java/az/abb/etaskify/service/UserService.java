package az.abb.etaskify.service;

import az.abb.etaskify.domain.auth.User;
import az.abb.etaskify.domain.auth.UserDto;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.exception.AuthException;
import az.abb.etaskify.mapper.UserMapper;
import az.abb.etaskify.repository.RoleRepository;
import az.abb.etaskify.repository.UserRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final Converter converter;

    public Optional<User> getByLogin(@NonNull String login) {//username ve login equalsIgnoreCase() ile yoxlanilir
        log.info("UserService/getByLogin method started");
        UserEntity user = userRepository.findByUsernameIgnoreCase(login).stream()
                .filter(u -> login.equalsIgnoreCase(u.getUsername()))
                .findFirst().orElseThrow(() -> new AuthException("User is not found"));
        log.info("UserService/getByLogin method  -> status:" + HttpStatus.OK);
        return converter.entityToDtoLogin(user);
    }

    public ResponseEntity<?> getUsers(){
        log.info("UserService/getUsers method started");
        List<UserEntity> users = userRepository.findAll();
        List<UserDto> usersDto = userMapper.usersToUsersDto(users);
        log.info("UserService/getUsers method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_GET.getValue(), usersDto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> addNewUser(UserDto user){
        log.info("UserService/addNewUser method started");
        Map<String, String> map = new HashMap<>();

        List<Long> rolesIds = roleRepository.findAllIds();

        if(userRepository.existsByUsernameIgnoreCase(user.getUsername()))
            map.put("username", "data already exists");
        if(userRepository.existsByEmailIgnoreCase(user.getEmail()))
            map.put("email", "data already exists");
        if(!CheckContains(user.getRoles(), rolesIds))
            map.put("roles", "problem with role id(s)");
        if(!map.isEmpty()) {
            map.forEach((k, v) -> log.error("UserService/addNewUser method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userEntity.setName(user.getFirstname());
        userEntity.setSurname(user.getLastname());
        userEntity.setEmail(user.getEmail());

        if(user.getRoles() != null)
            for (Long id : user.getRoles()){
                RoleEntity role = new RoleEntity();
                role.setId(id);
                userEntity.addRole(role);
            }

        userRepository.save(userEntity);
        UserDto userDto = userMapper.userToUserDto(userEntity);
        log.info("UserService/addNewUser method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_ADD.getValue(), userDto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> updateUser(UserDto user, Long userId){
        log.info("UserService/updateUser method started");
        Map<String, String> map = new HashMap<>();
        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if(userEntity.isEmpty())
            map.put("userId", "data doesn't exist");
        if(!map.isEmpty()) {
            map.forEach((k, v) -> log.error("UserService/updateUser method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        List<Long> rolesIds = roleRepository.findAllIds();
        Optional<UserEntity> userByUserName = userRepository.findByUsernameIgnoreCase(user.getUsername());
        Optional<UserEntity> userByEmail = userRepository.findByEmailIgnoreCase(user.getEmail());

        if(!CheckContains(user.getRoles(), rolesIds))
            map.put("roles", "problem with role id(s)");
        if(userByUserName.isPresent())
            if(!Objects.equals(userByUserName.get().getId(), userId) && userByUserName.get().getUsername().equalsIgnoreCase(user.getUsername()))
                map.put("username", "username already exists");
        if(userByEmail.isPresent())
            if(!Objects.equals(userByEmail.get().getId(), userId) && userByEmail.get().getEmail().equalsIgnoreCase(user.getEmail()))
                map.put("email", "email already exists");
        if(!map.isEmpty()){
            map.forEach((k, v) -> log.error("UserService/updateUser method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userEntity.get().setUsername(user.getUsername());
        userEntity.get().setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userEntity.get().setName(user.getFirstname());
        userEntity.get().setSurname(user.getLastname());
        userEntity.get().setEmail(user.getEmail());

        userEntity.get().getRoles().clear();

        for(Long id : user.getRoles()){
            RoleEntity role = new RoleEntity();
            role.setId(id);
            userEntity.get().addRole(role);
        }

        userRepository.save(userEntity.get());
        UserDto userDto = userMapper.userToUserDto(userEntity.get());

        log.info("UserService/updateUser method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_UPDATE.getValue(), userDto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUser(Long userId){
        log.info("UserService/deleteUser method started");
        Map<String, String> map = new HashMap<>();

        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if(userEntity.isEmpty())
            map.put("id", "user id doesn't exist");
        if(!map.isEmpty()){
            map.forEach((k, v) -> log.error("UserService/deleteUser method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userRepository.deleteById(userId);
        UserDto userDto = userMapper.userToUserDto(userEntity.get());
        log.info("UserService/deleteUser method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_DELETE.getValue(), userDto, null, HttpStatus.OK);
    }

    private boolean CheckContains(List<Long> userRolesIds, /*@NotNull*/ List<Long> allRolesIds){
        return allRolesIds.containsAll(userRolesIds);
    }
}
