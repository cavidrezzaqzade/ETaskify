package az.abb.etaskify.service;

import az.abb.etaskify.domain.JwtAuthentication;
import az.abb.etaskify.domain.Role;
import az.abb.etaskify.domain.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caci
 * @since 19.05.2022
 */

class JwtUtilsTest {
    private static JwtProvider jwtProvider;
    private static User user;

    @BeforeAll
    static void setUpAll() {
        jwtProvider = new JwtProvider("test", "test");

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().roleName("ADMIN").build());
        roles.add(Role.builder().roleName("USER").build());

        user = User.builder()
                .login("cavid")
                .firstName("cavvid")
                .password("dumb")
                .roles(roles)
                .build();
    }

    @Test
    @DisplayName("test generate method")
    void givenClaims_WhenGenerate_ThenCheck() {
        //given
        String accessToken = jwtProvider.generateAccessToken(user, 2L);
        final Claims claims = jwtProvider.getAccessClaims(accessToken);

        //when
        JwtAuthentication auth = JwtUtils.generate(claims);

        //then
        assertEquals(auth.getUsername(), "cavid");
        assertEquals(auth.getRoles().size(), 2);
        assertEquals(auth.getName(), "cavvid");
    }


}