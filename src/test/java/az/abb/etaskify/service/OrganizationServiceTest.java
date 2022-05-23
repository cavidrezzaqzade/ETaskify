package az.abb.etaskify.service;

import az.abb.etaskify.domain.AdminUserDto;
import az.abb.etaskify.domain.OrgUserDto;
import az.abb.etaskify.domain.OrganizationDto;
import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.mapper.AdminUserMapper;
import az.abb.etaskify.mapper.OrgMapper;
import az.abb.etaskify.repository.OrganizationRepository;
import az.abb.etaskify.repository.RoleRepository;
import az.abb.etaskify.repository.UserRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author caci
 * @since 23.05.2022
 */

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Spy
    private OrgMapper orgMapper;

    @Spy
    private AdminUserMapper adminUserMapper;

    private static OrganizationDto organizationDto;
    private static AdminUserDto userDto;
    private static OrgUserDto orgUserDto;
    private static OrganizationEntity organization;
    private static UserEntity user;

    @BeforeAll
    static void setUpAll(){
        organizationDto = OrganizationDto.builder()
                .orgName("test")
                .orgAddress("test")
                .orgPhone("test")
                .build();

        userDto = AdminUserDto.builder()
                .username("test")
                .password("test12")
                .email("exp@gmail.com")
                .build();

        orgUserDto = OrgUserDto.builder()
                .organizationDto(organizationDto)
                .adminDto(userDto)
                .build();

        organization = OrganizationEntity.builder()
                .orgAddress("test")
                .orgName("test")
                .orgPhone("test")
                .build();

        user = UserEntity.builder()
                .email("test@gmail.com")
                .username("test")
                .password("test12")
                .build();
    }

    @Test
    @DisplayName("add organization then ok")
    void givenOrganizationDto_WhenAddNewOrg_ThenOk() {
        //given
        given(organizationRepository.existsByOrgNameIgnoreCase(any())).willReturn(false);
        given(userRepository.existsByUsernameIgnoreCase(orgUserDto.getAdminDto().getUsername())).willReturn(false);
        given(userRepository.existsByEmailIgnoreCase(orgUserDto.getAdminDto().getEmail())).willReturn(false);
        given(organizationRepository.save(any())).willReturn(organization);
        given(orgMapper.orgToOrgDto(any())).willReturn(organizationDto);
        given(roleRepository.findByRoleNameIgnoreCase(any())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(user);
        given(adminUserMapper.userToUserDto(any())).willReturn(userDto);

        //when
        ResponseEntity<?> res = organizationService.addNewOrg(orgUserDto);

        //then
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        verify(organizationRepository, times(1)).existsByOrgNameIgnoreCase(any());
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("test");
        verify(userRepository, times(1)).existsByEmailIgnoreCase("exp@gmail.com");
        verify(organizationRepository, times(1)).save(any());
        verify(orgMapper, times(1)).orgToOrgDto(any());
        verify(roleRepository, times(1)).findByRoleNameIgnoreCase(any());
        verify(userRepository, times(1)).save(any());
        verify(adminUserMapper, times(1)).userToUserDto(any());
    }

    @Test
    @DisplayName("add organization then ok")
    void givenOrganizationDto_WhenAddNewOrg_ThenRoleNameFoundedOk() {
        //given
        given(organizationRepository.existsByOrgNameIgnoreCase(any())).willReturn(false);
        given(userRepository.existsByUsernameIgnoreCase(orgUserDto.getAdminDto().getUsername())).willReturn(false);
        given(userRepository.existsByEmailIgnoreCase(orgUserDto.getAdminDto().getEmail())).willReturn(false);
        given(organizationRepository.save(any())).willReturn(organization);
        given(orgMapper.orgToOrgDto(any())).willReturn(organizationDto);
        given(roleRepository.findByRoleNameIgnoreCase("ADMIN")).willReturn(Optional.of(RoleEntity.builder()
                        .id(1L)
                        .roleName("ADMIN")
                        .users(new HashSet<>(Set.of(user)))
                        .build()));
        given(userRepository.save(any())).willReturn(user);
        given(adminUserMapper.userToUserDto(any())).willReturn(userDto);

        //when
        ResponseEntity<?> res = organizationService.addNewOrg(orgUserDto);

        //then
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        verify(organizationRepository, times(1)).existsByOrgNameIgnoreCase(any());
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("test");
        verify(userRepository, times(1)).existsByEmailIgnoreCase("exp@gmail.com");
        verify(organizationRepository, times(1)).save(any());
        verify(orgMapper, times(1)).orgToOrgDto(any());
        verify(roleRepository, times(1)).findByRoleNameIgnoreCase(any());
        verify(userRepository, times(1)).save(any());
        verify(adminUserMapper, times(1)).userToUserDto(any());
    }

    @Test
    @DisplayName("add organization then exists by org name")
    void givenOrganizationDto_WhenAddNewOrg_ThenOrgNameExists() {
        //given
        given(organizationRepository.existsByOrgNameIgnoreCase(any())).willReturn(true);
        given(userRepository.existsByUsernameIgnoreCase(orgUserDto.getAdminDto().getUsername())).willReturn(false);
        given(userRepository.existsByEmailIgnoreCase(orgUserDto.getAdminDto().getEmail())).willReturn(false);

        //when
        ResponseEntity<?> res = organizationService.addNewOrg(orgUserDto);

        //then
        assertEquals(res.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        verify(organizationRepository, times(1)).existsByOrgNameIgnoreCase(any());
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("test");
        verify(userRepository, times(1)).existsByEmailIgnoreCase("exp@gmail.com");
    }

    @Test
    @DisplayName("add organization then exists by user name")
    void givenOrganizationDto_WhenAddNewOrg_ThenUserNameExists() {
        //given
        given(organizationRepository.existsByOrgNameIgnoreCase(any())).willReturn(false);
        given(userRepository.existsByUsernameIgnoreCase(orgUserDto.getAdminDto().getUsername())).willReturn(true);
        given(userRepository.existsByEmailIgnoreCase(orgUserDto.getAdminDto().getEmail())).willReturn(false);

        //when
        ResponseEntity<?> res = organizationService.addNewOrg(orgUserDto);

        //then
        assertEquals(res.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        verify(organizationRepository, times(1)).existsByOrgNameIgnoreCase(any());
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("test");
        verify(userRepository, times(1)).existsByEmailIgnoreCase("exp@gmail.com");
    }

    @Test
    @DisplayName("add organization then exists by email")
    void givenOrganizationDto_WhenAddNewOrg_ThenEmailExists() {
        //given
        given(organizationRepository.existsByOrgNameIgnoreCase(any())).willReturn(false);
        given(userRepository.existsByUsernameIgnoreCase(orgUserDto.getAdminDto().getUsername())).willReturn(false);
        given(userRepository.existsByEmailIgnoreCase(orgUserDto.getAdminDto().getEmail())).willReturn(true);

        //when
        ResponseEntity<?> res = organizationService.addNewOrg(orgUserDto);

        //then
        assertEquals(res.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        verify(organizationRepository, times(1)).existsByOrgNameIgnoreCase(any());
        verify(userRepository, times(1)).existsByUsernameIgnoreCase("test");
        verify(userRepository, times(1)).existsByEmailIgnoreCase("exp@gmail.com");
    }

}