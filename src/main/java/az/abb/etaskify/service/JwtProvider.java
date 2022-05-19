package az.abb.etaskify.service;

import az.abb.etaskify.domain.User;
import az.abb.etaskify.exception.excModel.GeneralJwtException;
import io.jsonwebtoken.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@PropertySource(value = {"file:src/main/resources/application.properties"})
public class JwtProvider {

    private final String jwtAccessSecret;
    private final String jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = jwtAccessSecret;
        this.jwtRefreshSecret = jwtRefreshSecret;
    }

    public String generateAccessToken(@NonNull User user, @NonNull Long expirationTime) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(expirationTime).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtAccessSecret)
                .claim("roles", user.getRoles())
                .claim("firstName", user.getFirstName())
                .compact();
    }

    public String generateRefreshToken(@NonNull User user, @NonNull Long expirationTime) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusHours(expirationTime).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(refreshExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(@NonNull String token) {
        return validateToken(token, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull String secret) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
            throw expEx;
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
            throw unsEx;
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
            throw mjEx;
        } catch (SignatureException siEx) {
            log.error("Invalid signature", siEx);
            throw siEx;
        } catch (Exception e) {
            log.error("invalid token", e);
            throw new GeneralJwtException("invalid token");
        }
//        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
