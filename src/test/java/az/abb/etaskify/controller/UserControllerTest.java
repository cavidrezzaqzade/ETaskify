package az.abb.etaskify.controller;

import az.abb.etaskify.domain.UserDto;
import az.abb.etaskify.filter.JwtFilter;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import az.abb.etaskify.service.UserService;
import com.fasterxml.jackson.databind.MapperFeature;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author caci
 * @since 18.05.2022
 */

@SuppressWarnings("unchecked")
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtFilter jwtFilter;

    private static UserDto userDto;
    private static UserDto userDto2;
    private static List<UserDto> users;
    private static UserDto userDtoUpdated;

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @BeforeAll
    static void setUpAll(){
        userDto = UserDto.builder().id(1L).roles(List.of(1L, 2L)).username("cavid").password("12345").build();
        userDto2 = UserDto.builder().id(2L).username("xanlar").password("dumb").build();

        users = List.of(userDto, userDto2);

        userDtoUpdated = UserDto.builder().id(1L).username("cavid2").password("dumb").build();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(jwtFilter)
                .apply(springSecurity())
                .build();

        //for disable JsonProperty.Access.WRITE_ONLY of UserDto
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check get users ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenNone_WhenGetUsers_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_GET.getValue();
        int usersSize = users.size();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_GET.getValue(), users, null, HttpStatus.OK);
        given(userService.getUsers()).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/api/users")
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
                .andExpect(jsonPath("$.data.size()").value(usersSize))
                .andExpect(jsonPath("$.errors", nullValue()));
    }

    @Test
    @DisplayName("check get users with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenNone_WhenGetUsers_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(get("/api/users")
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
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.errors", notNullValue()));
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check add user ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenUserDto_WhenAddUser_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_ADD.getValue();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_ADD.getValue(), userDto, null, HttpStatus.OK);
        given(userService.addNewUser(any())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

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
    @DisplayName("check add user with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenRoleDto_WhenAddUser_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

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

    /////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check update user ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenRoleDto_WhenUpdateUser_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_UPDATE.getValue();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_UPDATE.getValue(), userDtoUpdated, null, HttpStatus.OK);
        given(userService.updateUser(any(), any())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(put("/api/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect((jsonPath("$.data.username")
                        .value("cavid2")))
                .andExpect((jsonPath("$.data.password")
                        .value("dumb")))
                .andExpect(jsonPath("$.errors", nullValue()));
    }

    @Test
    @DisplayName("check update user with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenRoleDto_WhenUpdateUser_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

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

    /////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("check delete user ok")
    @WithMockUser(authorities = {ROLE_ADMIN})
    void givenRoleDto_WhenDeleteUser_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_DELETE.getValue();

        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_DELETE.getValue(), userDto, null, HttpStatus.OK);
        given(userService.deleteUser(1L)).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

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
    @DisplayName("check delete user with wrong role")
    @WithMockUser(authorities = {ROLE_USER})
    void givenRoleDto_WhenDeleteUser_ThenUnAuth() throws Exception {
        //given
        String message = "access denied for token";

        //when
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

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

    /////////////////////////////////////////////////////////////////////////////////////////

    
}