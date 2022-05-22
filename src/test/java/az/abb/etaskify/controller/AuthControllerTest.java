package az.abb.etaskify.controller;

import az.abb.etaskify.domain.jwt.JwtRequest;
import az.abb.etaskify.domain.jwt.JwtResponse;
import az.abb.etaskify.domain.jwt.RefreshJwtRequest;
import az.abb.etaskify.filter.JwtFilter;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import az.abb.etaskify.service.auth.AuthService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author caci
 * @since 19.05.2022
 */

@SuppressWarnings("unchecked")
@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtFilter jwtFilter;

    private static JwtRequest jwtRequest;
    private static RefreshJwtRequest refreshJwtRequest;

    @BeforeAll
    static void setUpAll(){
        jwtRequest = new JwtRequest();
        jwtRequest.setUsername("cavid");
        jwtRequest.setPassword("12345");

        refreshJwtRequest = new RefreshJwtRequest();
        refreshJwtRequest.setRefreshToken("dumb");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(jwtFilter)
                .apply(springSecurity())
                .build();
//        //for disable JsonProperty.Access.WRITE_ONLY of UserDto
//        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
    }

    @Test
    @DisplayName("test login ok")
    void givenUsernameAndPass_WhenLogin_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_GET.getValue();
        JwtResponse res = new JwtResponse("dumb", "dumb", 10L);
        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_GET.getValue(), res, null, HttpStatus.OK);
        given(authService.login(any())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jwtRequest)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.size()").value(4))
                .andExpect(jsonPath("$.errors", nullValue()));
    }

    @Test
    @DisplayName("test get new access token ok")
    void givenRefresh_WhenGetNewAccess_ThenOk() throws Exception {
        //given
        String message = Reason.SUCCESS_GET.getValue();
        JwtResponse res = new JwtResponse("dumb", "dumb", 10L);
        ResponseEntity response = MessageResponse.response(Reason.SUCCESS_GET.getValue(), res, null, HttpStatus.OK);
        given(authService.getAccessToken(any())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshJwtRequest)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.size()") //message, data, errors
                        .value(3)))
                .andExpect((jsonPath("$.message")
                        .value(message)))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.size()").value(4))
                .andExpect(jsonPath("$.errors", nullValue()));
    }
}