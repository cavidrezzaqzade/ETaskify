package az.abb.etaskify.service;

import az.abb.etaskify.domain.JwtAuthentication;
import az.abb.etaskify.domain.Role;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<Role> getRoles(Claims claims) {
        final List roles = claims.get("roles", List.class);
        final Set<Role> rolesSet = new HashSet<>();
        for(Object o : roles){
            LinkedHashMap<String, String> l = (LinkedHashMap<String, String>) o;
            Role r = new Role();
            r.setRoleName(l.get("roleName"));
            rolesSet.add(r);
        }
        return rolesSet;
    }
}
