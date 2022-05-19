package az.abb.etaskify.service;

import az.abb.etaskify.domain.Role;
import az.abb.etaskify.domain.User;
import az.abb.etaskify.domain.UserDto;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.exception.AuthException;
import az.abb.etaskify.mapper.UserMapper;
import az.abb.etaskify.repository.RoleRepository;
import az.abb.etaskify.repository.UserRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Spy
    private UserMapper userMapper;

    @Spy
    private Converter converter;

    private static UserEntity userEntity;
    private static UserEntity userEntity2;
    private static UserEntity userEntity3;
    private static UserDto userDto;
    private static UserDto userDto2;
    private static User user;

    @BeforeAll
    static void setUpAll(){

        Set<RoleEntity> roleEntities = new HashSet<>();
        roleEntities.add(RoleEntity.builder().id(1L).roleName("Admin").build());
        roleEntities.add(RoleEntity.builder().id(2L).roleName("User").build());

        userEntity = UserEntity.builder()
                .id(1L)
                .username("cavid")
                .password("12345")
                .roles(roleEntities)
                .build();

        userEntity2 = UserEntity.builder()
                .id(2L)
                .username("azer")
                .password("12345")
                .build();

        //for unique username check
        userEntity3 = UserEntity.builder()
                .id(3L)
                .username("cavid")
                .password("12345")
                .build();

        userDto = UserDto.builder()
                .username("cavid")
                .password("12345")
                .roles(List.of(1L, 2L))
                .build();

        userDto2 = UserDto.builder()
                .username("azer")
                .password("12345")
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().roleName("ADMIN").build());
        roles.add(Role.builder().roleName("USER").build());

        user = User.builder()
                .login("cavid")
                .password("12345")
                .roles(roles)
                .build();

    }

    @DisplayName("check getByLogin ok")
    @Test
    void givenLowerUserName_WhenGetByLogin_ThenOk() {
        //given
        String userName = "cavid";
        given(userRepository.findByUsernameIgnoreCase(userName)).willReturn(Optional.of(userEntity));
        given(converter.entityToDtoLogin(userEntity)).willReturn(Optional.of(user));

        //when
        Optional<User> res = userService.getByLogin(userName);

        //then
        assertEquals(res, Optional.of(user));
        verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
        verify(converter, times(1)).entityToDtoLogin(userEntity);
    }

    @DisplayName("check getByLogin throws authException")
    @Test
    void givenLowerUserName_WhenGetByLogin_ThenAuthException() {
        //given
        String userName = "cavid";
        given(userRepository.findByUsernameIgnoreCase(userName)).willReturn(Optional.of(userEntity2));

        //when
        Assertions.assertThrows(AuthException.class, () -> userService.getByLogin(userName));

        //then
        verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
        verify(converter, never()).entityToDtoLogin(userEntity);
    }

    //////////////////////////////////////////////////////////////////////////////

    /**
     * we should not test private methods.
     */
//    @DisplayName("check entityToDtoLogin ok")
//    @Test
//    void givenUserEntity_WhenEntityToDtoLogin_ThenUser() {
//    }

    //////////////////////////////////////////////////////////////////////////////

    @DisplayName("check get all users ok")
    @Test
    void givenNone_WhenGetUsers_ThenOk() {
        //given
        List<UserEntity> users = List.of(userEntity, userEntity2);
        List<UserDto> usersDto = List.of(userDto, userDto2);
        given(userRepository.findAll()).willReturn(users);
        given(userMapper.usersToUsersDto(users)).willReturn(usersDto);

        //when
        ResponseEntity<?> res = userService.getUsers();

        //then
        assertEquals(res, MessageResponse.response(Reason.SUCCESS_ADD.getValue(), List.of(userDto, userDto2), null, HttpStatus.OK));
        verify(userRepository).findAll();
    }

    @DisplayName("check get all users empty list ok")
    @Test
    void givenNone_WhenGetUsers_ThenEmptyListOk() {
        //given
        given(userRepository.findAll()).willReturn(List.of());
        given(userMapper.usersToUsersDto(List.of())).willReturn(List.of());

        //when
        ResponseEntity<?> res = userService.getUsers();

        //then
        assertEquals(res, MessageResponse.response(Reason.SUCCESS_ADD.getValue(), List.of(), null, HttpStatus.OK));
        verify(userRepository).findAll();
    }

    //////////////////////////////////////////////////////////////////////////////

    @DisplayName("check add new user ok")
    @Test
    void givenUserDto_WhenAddNewUser_ThenOk() {
        //given
        given(userRepository.existsByUsernameIgnoreCase("cavid")).willReturn(false);
        given(roleRepository.findAllIds()).willReturn(List.of(1L, 2L));
        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);
        given(userMapper.userToUserDto(any(UserEntity.class))).willReturn(userDto);

        //when
        ResponseEntity<?> res = userService.addNewUser(userDto);

        //then
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo(MessageResponse.response(Reason.SUCCESS_ADD.getValue(), userDto, null, HttpStatus.OK));
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("cavid");
        verify(roleRepository, times(1)).findAllIds();
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).userToUserDto(any(UserEntity.class));
    }

    @DisplayName("check add new user 422")
    @Test
    void givenUserDto_WhenAddNewUser_ThenUnprocessable() {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("username", "data already exists");
        given(userRepository.existsByUsernameIgnoreCase("cavid")).willReturn(true);

        //when
        ResponseEntity<?> res = userService.addNewUser(userDto);

        //then
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo(MessageResponse.response(Reason.SUCCESS_ADD.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY));
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("cavid");
    }

    @DisplayName("check add new user 422 due to roleIds")
    @Test
    void givenUserDto_WhenAddNewUser_ThenUnproccessable() {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("roles", "problem with role id(s)");
        given(userRepository.existsByUsernameIgnoreCase("cavid")).willReturn(false);
        given(roleRepository.findAllIds()).willReturn(List.of(-1L, -2L));

        //when
        ResponseEntity<?> res = userService.addNewUser(userDto);

        //then
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo(MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY));
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("cavid");
        verify(roleRepository, times(1)).findAllIds();
    }

    //////////////////////////////////////////////////////////////////////////////

    @DisplayName("check update user by id not found")
    @Test
    void givenUserDtoAndUserId_WhenUpdateUser_ThenNotFound() {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("userId", "data doesn't exist");
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when
        ResponseEntity<?> responseOK = userService.updateUser(userDto, userId);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY));
        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("check update user by id not found")
    @Test
    void givenUserDtoAndUserId_WhenUpdateUser_ThenOk() {
        //given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(roleRepository.findAllIds()).willReturn(List.of(1L,2L));
        given(userRepository.findByUsernameIgnoreCase("cavid")).willReturn(Optional.of(userEntity));
        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);
        given(userMapper.userToUserDto(any(UserEntity.class))).willReturn(userDto);

        //when
        ResponseEntity<?> res = userService.updateUser(userDto, userId);

        //then
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo(MessageResponse.response(Reason.SUCCESS_UPDATE.getValue(), userDto, null, HttpStatus.OK));
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findAllIds();
        verify(userRepository, times(1)).findByUsernameIgnoreCase("cavid");
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).userToUserDto(any(UserEntity.class));
    }

    @DisplayName("check update user by id invalid role ids")
    @Test
    void givenUserDtoAndUserId_WhenUpdateUser_ThenInvalidRoleIds() {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("roles","problem with role id(s)");
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(roleRepository.findAllIds()).willReturn(List.of(2L,3L));

        //when
        ResponseEntity<?> res = userService.updateUser(userDto, userId);

        //then
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo(MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY));
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findAllIds();
    }

    @DisplayName("check update user by id unique roleName violation")
    @Test
    void givenUserDtoAndUserId_WhenUpdateUser_ThenUniqueViolation() {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("username","username already exists");
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(roleRepository.findAllIds()).willReturn(List.of(1L,2L));
        given(userRepository.findByUsernameIgnoreCase("cavid")).willReturn(Optional.of(userEntity3));

        //when
        ResponseEntity<?> res = userService.updateUser(userDto, userId);

        //then
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo(MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY));
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findAllIds();
        verify(userRepository, times(1)).findByUsernameIgnoreCase("cavid");
    }

    //////////////////////////////////////////////////////////////////////////////

    @DisplayName("check delete user by id 422")
    @Test
    void givenUserId_WhenDeleteUser_ThenUnprocessable() {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("id", "user id doesn't exist");
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when
        ResponseEntity<?> responseOK = userService.deleteUser(userId);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY));
        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("check delete user by id ok")
    @Test
    void givenUserId_WhenDeleteUser_ThenOk() {
        //given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userMapper.userToUserDto(any(UserEntity.class))).willReturn(userDto);

        //when
        ResponseEntity<?> responseOK = userService.deleteUser(userId);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(MessageResponse.response(Reason.SUCCESS_ADD.getValue(), userDto, null, HttpStatus.OK));
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).userToUserDto(any(UserEntity.class));
    }

}