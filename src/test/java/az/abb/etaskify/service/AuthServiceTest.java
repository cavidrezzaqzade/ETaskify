package az.abb.etaskify.service;

import az.abb.etaskify.domain.jwt.JwtRequest;
import az.abb.etaskify.domain.auth.Role;
import az.abb.etaskify.domain.auth.User;
import az.abb.etaskify.exception.AuthException;
import az.abb.etaskify.service.auth.AuthService;
import az.abb.etaskify.service.auth.JwtProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author caci
 * @since 19.05.2022
 */

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static User user;
    private static JwtRequest jwtRequest;
    private static JwtRequest jwtRequestWithWrongPass;
    private static final Map<String, String> refreshStorage = new HashMap<>();

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeAll
    static void setUpAll(){
        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().roleName("ADMIN").build());
        roles.add(Role.builder().roleName("USER").build());

        user = User.builder()
                .login("cavid")
                .firstName("cavid")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .roles(roles)
                .build();

        jwtRequest = new JwtRequest();
        jwtRequest.setLogin("cavid");
        jwtRequest.setPassword("12345");

        jwtRequestWithWrongPass = new JwtRequest();
        jwtRequestWithWrongPass.setLogin("cavid");
        jwtRequestWithWrongPass.setPassword("1234");

        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusHours(10).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        refreshStorage.put(user.getLogin(), Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(refreshExpiration)
                .signWith(SignatureAlgorithm.HS512, "test")
                .compact());
    }

    ////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("test login user not found")
    void givenLogin_WhenLogin_ThenUserNotFound() {
        //given
        String exception = "not found";
        String userName = jwtRequest.getLogin();
        given(userService.getByLogin(userName)).willReturn(Optional.empty());

        //when
        AuthException thrown = Assertions.assertThrows(AuthException.class, () -> authService.login(jwtRequest));

        //then
        assertTrue(thrown.getMessage().contains(exception));
        verify(userService, times(1)).getByLogin(userName);
    }

    @Test
    @DisplayName("test login wrong pass")
    void givenLogin_WhenLogin_ThenWrongPass() {
        //given
        String passException = "wrong password";
        String userName = jwtRequestWithWrongPass.getLogin();
        given(userService.getByLogin(userName)).willReturn(Optional.of(user));

        //when
        AuthException thrown = Assertions.assertThrows(AuthException.class, () -> authService.login(jwtRequestWithWrongPass));

        //then
        assertTrue(thrown.getMessage().equalsIgnoreCase(passException));
        verify(userService, times(1)).getByLogin(userName);
    }

    @Test
    @DisplayName("test login ok")
    void givenLogin_WhenLogin_ThenOk() {
        //given
        String userName = jwtRequest.getLogin();
        given(userService.getByLogin(userName)).willReturn(Optional.of(user));

        //when
        ResponseEntity<?> response = authService.login(jwtRequest);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        verify(userService, times(1)).getByLogin(userName);
    }

    ////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("test get access token wrong refresh")
    void givenRefresh_WhenGenerate_ThenInvalidJwt() {
        //given
        String refresh = refreshStorage.get(user.getLogin());
        String passException = "Invalid";
        given(jwtProvider.validateRefreshToken(refresh)).willReturn(false);

        //when
        AuthException thrown = Assertions.assertThrows(AuthException.class, () -> authService.getAccessToken(refresh));

        //then
        assertTrue(thrown.getMessage().toLowerCase(Locale.ROOT).contains(passException.toLowerCase(Locale.ROOT)));
        verify(jwtProvider, times(1)).validateRefreshToken(refresh);
    }

//    @Test
//    @DisplayName("test generate user not found")
//    void givenLogin_WhenGenerate_ThenUserNotFound() {
//        //given
//        String refresh = refreshStorage.get(user.getLogin());
//        String exception = "not found";
//        String userName = jwtRequestWithWrongPass.getLogin();
//
//        given(userService.getByLogin(jwtRequest.getLogin())).willReturn(Optional.of(user));
//        given(jwtProvider.validateRefreshToken(refresh)).willReturn(true);
//        given(jwtProvider.getRefreshClaims(refresh)).willReturn(Jwts.parser().setSigningKey("test").parseClaimsJws(refresh).getBody());
//        given(userService.getByLogin(userName)).willReturn(Optional.empty());
//
//        //when
//        authService.login(jwtRequest);
//        AuthException thrown = Assertions.assertThrows(AuthException.class, () -> authService.getAccessToken(refresh));
//
//        //then
////        assertTrue(thrown.getMessage().toLowerCase(Locale.ROOT).contains(exception.toLowerCase(Locale.ROOT)));
//        verify(jwtProvider, times(1)).validateRefreshToken(refresh);
//        verify(jwtProvider,times(1)).getRefreshClaims(refresh);
//        verify(userService, times(1)).getByLogin(userName);
//    }

//    private static String getRefreshForTest(long expirationTime){
//        final LocalDateTime now = LocalDateTime.now();
//        final Instant refreshExpirationInstant = now.plusHours(expirationTime).atZone(ZoneId.systemDefault()).toInstant();
//        final Date refreshExpiration = Date.from(refreshExpirationInstant);
//        return Jwts.builder()
//                .setSubject(user.getLogin())
//                .setExpiration(refreshExpiration)
//                .signWith(SignatureAlgorithm.HS512, "test")
//                .compact();
//    }
}