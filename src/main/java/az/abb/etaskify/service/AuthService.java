package az.abb.etaskify.service;

import az.abb.etaskify.domain.jwt.JwtRequest;
import az.abb.etaskify.domain.jwt.JwtResponse;
import az.abb.etaskify.domain.auth.User;
import az.abb.etaskify.exception.AuthException;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Long accessTokenExpTimeMinutes = 10L;
    private final Long refreshTokenExpTimeHours = 12L;

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;

    public ResponseEntity<?> login(@NonNull JwtRequest authRequest) {
        log.info("authService/login method started");
        final User user = userService.getByLogin(authRequest.getLogin())
                .orElseThrow(() -> new AuthException("User is not found"));
//        if (user.getPassword().equals(authRequest.getPassword())) {
        if (new BCryptPasswordEncoder().matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user, accessTokenExpTimeMinutes);
            final String refreshToken = jwtProvider.generateRefreshToken(user, refreshTokenExpTimeHours);
            refreshStorage.put(user.getLogin(), refreshToken);
            log.info("authService/login method ended -> status:" + HttpStatus.OK);
            return MessageResponse.response(Reason.SUCCESS_GET.getValue(), new JwtResponse(accessToken, refreshToken, accessTokenExpTimeMinutes * 60000), null, HttpStatus.OK);
//            return new JwtResponse(accessToken, refreshToken);
        } else {
            log.error("authService/login method ended with wrong password -> status:" + HttpStatus.UNPROCESSABLE_ENTITY);
            throw new AuthException("wrong password");
        }
    }

    public ResponseEntity<?> getAccessToken(@NonNull String refreshToken) {
        log.info("authService/getAccessToken method started");
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.getByLogin(login)
                        .orElseThrow(() -> new AuthException("User is not found"));
                final String accessToken = jwtProvider.generateAccessToken(user , accessTokenExpTimeMinutes * 60000);
                final String newRefreshToken;
                if(claims.getExpiration().getTime() - new Date().getTime() < ( accessTokenExpTimeMinutes + 1L)){//check if old refresh does not expired then return it instead new one // +1 because refresh token needs rest
                    newRefreshToken = jwtProvider.generateRefreshToken(user, refreshTokenExpTimeHours);
                    refreshStorage.remove(user.getLogin());
                    refreshStorage.put(user.getLogin(), newRefreshToken);
                }
                else{
                    newRefreshToken = refreshStorage.get(user.getLogin());
                }
                log.info("authService/getAccessToken method ended -> status:" + HttpStatus.OK);
                return MessageResponse.response(Reason.SUCCESS_GET.getValue(), new JwtResponse(accessToken, newRefreshToken, accessTokenExpTimeMinutes), null, HttpStatus.OK);
//                return new JwtResponse(accessToken, null);
            }
        }
        log.error("authService/getAccessToken method ended with invalid jwt token -> status:" + HttpStatus.UNPROCESSABLE_ENTITY);
        throw new AuthException("Invalid JWT token");
//        return new JwtResponse(null, null);
    }
}
