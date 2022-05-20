package az.abb.etaskify.service;

import az.abb.etaskify.domain.auth.Role;
import az.abb.etaskify.domain.auth.User;
import az.abb.etaskify.exception.excModel.GeneralJwtException;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private static JwtProvider jwtProvider;
    private static User user;

    @BeforeAll
    static void setUpAll(){

        jwtProvider = new JwtProvider("test", "test");

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().roleName("ADMIN").build());
        roles.add(Role.builder().roleName("USER").build());

        user = User.builder()
                .login("cavid")
                .password("12345")
                .roles(roles)
                .build();
    }

    @DisplayName("check generateAccessToken ok")
    @Test
    void givenUser_WhenGenerateAccessToken_ThenOk() {
        //given
        //when
        String accessToken = jwtProvider.generateAccessToken(user, 2L);

        //then
        assertFalse(accessToken.isEmpty());
        assertFalse(accessToken.isBlank());
    }

    @DisplayName("check generateRefreshToken ok")
    @Test
    void givenUser_WhenGenerateRefreshToken_ThenOk() {
        //given
        //when
        String accessToken = jwtProvider.generateRefreshToken(user, 2L);

        //then
        assertFalse(accessToken.isEmpty());
        assertFalse(accessToken.isBlank());
    }

    @DisplayName("check validateAccessToken ok")
    @Test
    void givenToken_WhenValidateAccessToken_ThenOk() {
        //given
        String emptyToken = jwtProvider.generateAccessToken(user, 1L);

        //when
        boolean check = jwtProvider.validateAccessToken(emptyToken);

        //then
        assertTrue(check);
    }

    @DisplayName("check validateAccessToken GeneralJwtException")
    @Test
    void givenToken_WhenValidateAccessToken_ThenGeneralJwtException() {
        //given
        String emptyToken = "";

        //when
        Assertions.assertThrows(GeneralJwtException.class, () -> jwtProvider.validateAccessToken(emptyToken));
    }

    @DisplayName("check validateAccessToken ExpiredJwtException")
    @Test
    void givenToken_WhenValidateAccessToken_ThenExpiredJwtException() {
        //given
        String expiredToken = jwtProvider.generateAccessToken(user, 0L);

        //when
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtProvider.validateAccessToken(expiredToken));
    }

    @DisplayName("check validateAccessToken MalformedJwtException")
    @Test
    void givenToken_WhenValidateAccessToken_ThenMalformedJwtException() {
        //given
        String token = "dumb";

        //when
        Assertions.assertThrows(MalformedJwtException.class, () -> jwtProvider.validateAccessToken(token));
    }

    @DisplayName("check validateAccessToken SignatureException")
    @Test
    void givenToken_WhenValidateAccessToken_ThenSignatureException() {
        //given
        String token = "eyJhbGciOiJIUzUxMiJ9.em.A";//dumb jwt for test purpose

        //when
        Assertions.assertThrows(SignatureException.class, () -> jwtProvider.validateAccessToken(token));
    }

    @DisplayName("check validateAccessToken UnsupportedJwtException")
    @Test
    void givenToken_WhenValidateAccessToken_ThenUnsupportedJwtException() {
        //given
        String token = Jwts.builder()
                .setSubject(user.getLogin())
                .compact(); //token without signature

        //when
        Assertions.assertThrows(UnsupportedJwtException.class, () -> jwtProvider.validateAccessToken(token));
    }

    @DisplayName("check validateRefreshToken")
    @Test
    void givenToken_WhenValidateRefreshToken_ThenException() {
        //given
        String emptyToken = "";

        //when
        Assertions.assertThrows(GeneralJwtException.class, () -> jwtProvider.validateRefreshToken(emptyToken));
    }

    @DisplayName("check validateRefreshToken")
    @Test
    void givenToken_WhenValidateRefreshToken_ThenException2() {
        //given
        String emptyToken = "a";

        //when
        Assertions.assertThrows(MalformedJwtException.class, () -> jwtProvider.validateRefreshToken(emptyToken));
    }

    @DisplayName("check getAccessClaims")
    @Test
    void givenToken_WhenGetAccessClaims_ThenException() {
        //given
        String emptyToken = "a";

        //when
        Assertions.assertThrows(MalformedJwtException.class, () -> jwtProvider.getAccessClaims(emptyToken));
    }

    @DisplayName("check getRefreshClaims")
    @Test
    void givenToken_WhenGetRefreshClaims_ThenException() {
        //given
        String emptyToken = "";

        //when
        Assertions.assertThrows(IllegalArgumentException.class, () -> jwtProvider.getRefreshClaims(emptyToken));
    }
}