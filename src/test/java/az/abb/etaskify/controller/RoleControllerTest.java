package az.abb.etaskify.controller;

import az.abb.etaskify.domain.auth.RoleDto;
import az.abb.etaskify.filter.JwtFilter;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import az.abb.etaskify.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unchecked")
@AutoConfigureMockMvc(/*addFilters = false*/)
//@WebMvcTest(RoleController.class)
@SpringBootTest
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtFilter jwtFilter;

    private static RoleDto roleDto;
    private static RoleDto roleDto2;
    private static List<RoleDto> roles;
    private static RoleDto roleDtoUpdated;

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @BeforeAll
    static void setUpAll(){
        roleDto = RoleDto.builder().id(1L).roleName("ADMIN").build();
        roleDto2 = RoleDto.builder().id(2L).roleName("USER").build();

        roles = List.of(roleDto, roleDto2);

        roleDtoUpdated = RoleDto.builder().id(1L).roleName("ADMIN2").build();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(jwtFilter)
                .apply(springSecurity())
                .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check get roles ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenNone_WhenGetRoles_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_GET.getValue();
        int rolesSize = roles.size();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_GET.getValue(), roles, null, HttpStatus.OK);
        given(roleService.getRoles()).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect(jsonPath("$.data.size()").value(rolesSize))
                .andExpect(jsonPath("$.errors", nullValue()));
    }

    @Test
    @DisplayName("check get roles with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenTestRoles_WhenGetRoles_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect(jsonPath("$.errors", notNullValue()))
                .andExpect(jsonPath("$.data",nullValue()));

    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check add role ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenRoleDto_WhenAddRole_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_ADD.getValue();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_ADD.getValue(), roleDto, null, HttpStatus.OK);
        given(roleService.addNewRole(any())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()));
    }

    @Test
    @DisplayName("check add role with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenRoleDto_WhenAddRole_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect((jsonPath("$.data", nullValue() )))
                .andExpect(jsonPath("$.errors", notNullValue()));
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check update role ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenRoleDto_WhenUpdateRole_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_UPDATE.getValue();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_UPDATE.getValue(), roleDtoUpdated, null, HttpStatus.OK);
        given(roleService.updateRole(any(), any())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(put("/api/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect((jsonPath("$.data.roleName")
                        .value("ADMIN2")))
                .andExpect(jsonPath("$.errors", nullValue()));
    }

    @Test
    @DisplayName("check update role with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenRoleDto_WhenUpdateRole_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(put("/api/roles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect((jsonPath("$.data", nullValue() )))
                .andExpect(jsonPath("$.errors", notNullValue()));
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check delete role ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenRoleDto_WhenDeleteRole_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_DELETE.getValue();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_DELETE.getValue(), roleDto, null, HttpStatus.OK);
        given(roleService.deleteRole(1L)).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(delete("/api/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect((jsonPath("$.data", notNullValue() )))
                .andExpect(jsonPath("$.errors", nullValue()));
    }

    @Test
    @DisplayName("check delete role with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenRoleDto_WhenDeleteRole_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(delete("/api/roles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect((jsonPath("$.data", nullValue() )))
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}