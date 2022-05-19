package az.abb.etaskify.service;

import az.abb.etaskify.domain.RoleDto;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.mapper.RoleMapper;
import az.abb.etaskify.repository.RoleRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import org.junit.jupiter.api.*;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Spy
    private RoleMapper roleMapper;

    private static RoleEntity roleEntity;
    private static RoleEntity roleEntity2;
    private static RoleEntity roleEntityEmptyUsers;
    private static RoleEntity roleEntity3;
    private static RoleDto roleDto;
    private static RoleDto roleDto2;
    private static ResponseEntity<?> responseModelDTO;
    private static ResponseEntity<?> responseModelDTO2;
    private static ResponseEntity<?> responseModelDTOUnprocessableAddRole;
    private static ResponseEntity<?> responseModelDTOUnprocessableFk;
    private static ResponseEntity<?> responseModelDTOUnprocessableDoesNotExist;
    private static ResponseEntity<?> responseModelDTOUnprocessableAlreadyExist;
    private static ResponseEntity<?> responseModelDTOList;
    private static ResponseEntity<?> responseModelDTOEmptyList;

    @BeforeAll
    static void setUpAll(){
        roleEntity = RoleEntity.builder()
                .id(1L)
                .roleName("Admin")
                .build();
        roleEntity2 = RoleEntity.builder()
                .id(2L)
                .roleName("User")
                .build();

        roleEntityEmptyUsers = RoleEntity.builder()
                .id(1L)
                .roleName("Admin")
                .build();

        roleEntity3 = RoleEntity.builder()
                .id(3L)
                .roleName("Admin")
                .build();

        UserEntity user1 = UserEntity.builder()
                .username("cavid")
                .password("12345")
                .name("caci")
                .build();

        roleEntity.setUsers(Set.of(user1));
        roleEntityEmptyUsers.setUsers(Set.of());

        roleDto = RoleDto.builder()
                .roleName("Admin")
                .build();
        roleDto2 = RoleDto.builder()
                .roleName("User")
                .build();

        responseModelDTOList = MessageResponse.response(Reason.SUCCESS_ADD.getValue(), List.of(roleDto, roleDto2), null, HttpStatus.OK);
        responseModelDTO = MessageResponse.response(Reason.SUCCESS_ADD.getValue(), roleDto, null, HttpStatus.OK);
        responseModelDTO2 = MessageResponse.response(Reason.SUCCESS_ADD.getValue(), roleDto2, null, HttpStatus.OK);
        responseModelDTOEmptyList = MessageResponse.response(Reason.SUCCESS_GET.getValue(), List.of(), null, HttpStatus.OK);

        Map<String, String> map1 = new HashMap<>();
        map1.put("roleName", "already exists");
        responseModelDTOUnprocessableAddRole = MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map1, HttpStatus.UNPROCESSABLE_ENTITY);

        Map<String, String> map2 = new HashMap<>();
        map2.put("roleId", "foreign key constraint violation");
        responseModelDTOUnprocessableFk = MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map2, HttpStatus.UNPROCESSABLE_ENTITY);

        Map<String, String> map3 = new HashMap<>();
        map3.put("roleId", "role does not exist");
        responseModelDTOUnprocessableDoesNotExist = MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map3, HttpStatus.UNPROCESSABLE_ENTITY);

        Map<String, String> map4 = new HashMap<>();
        map4.put("roleName", "roleName already exists");
        responseModelDTOUnprocessableAlreadyExist = MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map4, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @DisplayName("check add new role ok")
    @Test
    void givenRoleDto_WhenAddNewRole_ThenOK() {
        //given
        given(roleRepository.existsByRoleNameIgnoreCase("Admin")).willReturn(false);
        given(roleRepository.save(any(RoleEntity.class))).willReturn(roleEntity);
//        given(roleMapper.roleDtoToRole(roleDto)).willReturn(roleEntity); //should we use mapper in the service or ?
        given(roleMapper.roleToRoleDto(any(RoleEntity.class))).willReturn(roleDto);

        //when
        ResponseEntity<?> responseOK = roleService.addNewRole(roleDto);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(responseModelDTO);
        verify(roleRepository, times(1)).existsByRoleNameIgnoreCase("Admin");
        verify(roleRepository, times(1)).save(any(RoleEntity.class));
        verify(roleMapper, times(1)).roleToRoleDto(any(RoleEntity.class));
    }

    @DisplayName("check add new role 422")
    @Test
    void givenRoleDto_WhenAddNewRole_ThenUnprocessable() {
        //given
        given(roleRepository.existsByRoleNameIgnoreCase("Admin")).willReturn(true);

        //when
        ResponseEntity<?> responseUnprocessable = roleService.addNewRole(roleDto);

        //then
        assertThat(responseUnprocessable).isNotNull();
        assertThat(responseUnprocessable).isEqualTo(responseModelDTOUnprocessableAddRole);
        verify(roleRepository, times(1)).existsByRoleNameIgnoreCase("Admin");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @DisplayName("check get all roles ok")
    @Test
    void givenNone_WhenGetRoles_ThenOK() {
        //given
        List<RoleEntity> roles = List.of(roleEntity, roleEntity2);
        List<RoleDto> roleDtos = List.of(roleDto, roleDto2);
        given(roleRepository.findAll()).willReturn(roles);
        given(roleMapper.rolesToRoleDtos(roles)).willReturn(roleDtos);

        //when
        ResponseEntity<?> a = roleService.getRoles();
        
        //then
        assertEquals(a, responseModelDTOList);
        verify(roleRepository).findAll();
    }

    @DisplayName("check get all roles empty list ok")
    @Test
    void givenNone_WhenGetRoles_ThenEmptyListOK() {
        //given
        given(roleRepository.findAll()).willReturn(Collections.emptyList());
        given(roleMapper.rolesToRoleDtos(Collections.emptyList())).willReturn(Collections.emptyList());
        
        //when
        ResponseEntity<?> a = roleService.getRoles();

        //then
        assertEquals(a, responseModelDTOEmptyList);
        verify(roleRepository).findAll();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @DisplayName("check delete role by id ok")
    @Test
    void givenRoleId_WhenDeleteRole_ThenOK() {
        //given
        Long roleID = 1L;
        given(roleRepository.findById(roleID)).willReturn(Optional.of(roleEntityEmptyUsers));
        given(roleMapper.roleToRoleDto(any(RoleEntity.class))).willReturn(roleDto);

        //when
        ResponseEntity<?> responseOK = roleService.deleteRole(roleID);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(responseModelDTO);
        verify(roleRepository, times(1)).findById(roleID);
        verify(roleMapper).roleToRoleDto(any(RoleEntity.class));
    }

    @DisplayName("check delete role by id fk violation 422")
    @Test
    void givenRoleId_WhenDeleteRole_ThenConstraintViolation() {
        //given
        Long roleID = 1L;
        given(roleRepository.findById(roleID)).willReturn(Optional.of(roleEntity));
//        given(roleMapper.roleToRoleDto(any(RoleEntity.class))).willReturn(roleDto);

        //when
        ResponseEntity<?> responseOK = roleService.deleteRole(roleID);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(responseModelDTOUnprocessableFk);
        verify(roleRepository, times(1)).findById(roleID);
//        verify(roleMapper, times(0)).roleToRoleDto(any(RoleEntity.class));
    }

    @DisplayName("check delete role by id does not exist")
    @Test
    void givenRoleId_WhenDeleteRole_ThenDoesNotExist() {
        //given
        Long roleId = 1L;
        given(roleRepository.findById(roleId)).willReturn(Optional.empty());

        //when
        ResponseEntity<?> responseOK = roleService.deleteRole(roleId);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(responseModelDTOUnprocessableDoesNotExist);
        verify(roleRepository, times(1)).findById(roleId);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @DisplayName("check update does not exist")
    @Test
    void givenRoleId_WhenUpdateRole_ThenDoesNotExist() {
        //given
        Long roleId = 1L;
        given(roleRepository.findById(roleId)).willReturn(Optional.empty());

        //when
        ResponseEntity<?> responseOK = roleService.updateRole(roleDto, roleId);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(responseModelDTOUnprocessableDoesNotExist);
        verify(roleRepository, times(1)).findById(roleId);
    }

    @DisplayName("check update unique roleName violation")
    @Test
    void givenRoleId_WhenUpdateRole_ThenUniqueRViolation() {
        //given
        Long roleId = 1L;
        given(roleRepository.findById(roleId)).willReturn(Optional.of(roleEntity));
        given(roleRepository.findByRoleNameIgnoreCase(any())).willReturn(Optional.of(roleEntity3));
//        given(roleMapper.roleDtoToRole(roleDto)).willReturn(roleEntity); //should we use mapper in the service or ?
//        given(roleMapper.roleToRoleDto(any(RoleEntity.class))).willReturn(roleDto);

        //when
        ResponseEntity<?> responseOK = roleService.updateRole(roleDto, roleId);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(responseModelDTOUnprocessableAlreadyExist);
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).findByRoleNameIgnoreCase(any());
    }

    @DisplayName("check update ok")
    @Test
    void givenRoleId_WhenUpdateRole_ThenOK() {
        //given
        Long roleId = 1L;
        given(roleRepository.findById(roleId)).willReturn(Optional.of(roleEntity));
        given(roleRepository.findByRoleNameIgnoreCase(any())).willReturn(Optional.empty());
        given(roleRepository.save(roleEntity)).willReturn(roleEntity2);
        given(roleMapper.roleToRoleDto(roleEntity)).willReturn(roleDto2);

        //when
        ResponseEntity<?> responseOK = roleService.updateRole(roleDto, roleId);

        //then
        assertThat(responseOK).isNotNull();
        assertThat(responseOK).isEqualTo(responseModelDTO2);
        assertEquals("User", roleDto2.getRoleName());
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).findByRoleNameIgnoreCase(any());
        verify(roleRepository, times(1)).save(roleEntity);
    }
}