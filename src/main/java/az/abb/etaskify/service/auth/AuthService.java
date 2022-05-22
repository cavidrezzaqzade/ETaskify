package az.abb.etaskify.service.auth;

import az.abb.etaskify.domain.jwt.JwtRequest;
import az.abb.etaskify.domain.jwt.JwtResponse;
import az.abb.etaskify.domain.auth.User;
import az.abb.etaskify.exception.AuthException;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import az.abb.etaskify.service.UserService;
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

    private enum Auth{
        LOGIN("AuthService/login"),
        GET_ACCESS_TOKEN("AuthService/getAccessToken");

        private final String methodName;

        Auth(String methodName){
            this.methodName = methodName;
        }

        public String getName(){
            return methodName;
        }
    }

    public ResponseEntity<?> login(@NonNull JwtRequest authRequest) {
        log.info(Auth.LOGIN.getName() + " method started");
        final User user = userService.getByLogin(authRequest.getUsername())
                .orElseThrow(() -> new AuthException("User is not found"));
//        if (user.getPassword().equals(authRequest.getPassword())) {
        if (new BCryptPasswordEncoder().matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user, accessTokenExpTimeMinutes);
            final String refreshToken = jwtProvider.generateRefreshToken(user, refreshTokenExpTimeHours);
            refreshStorage.put(user.getLogin(), refreshToken);
            log.info(Auth.LOGIN.getName() + " method ended -> status:" + HttpStatus.OK);
            return MessageResponse.response(Reason.SUCCESS_GET.getValue(), new JwtResponse(accessToken, refreshToken, accessTokenExpTimeMinutes * 60000), null, HttpStatus.OK);
//            return new JwtResponse(accessToken, refreshToken);
        } else {
            log.error(Auth.LOGIN.getName() + " method ended with wrong password -> status:" + HttpStatus.UNPROCESSABLE_ENTITY);
            throw new AuthException("wrong password");
        }
    }

    public ResponseEntity<?> getAccessToken(@NonNull String refreshToken) {
        log.info(Auth.GET_ACCESS_TOKEN.getName() + " method started");
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.getByLogin(login)
                        .orElseThrow(() -> new AuthException("User is not found"));
                final String accessToken = jwtProvider.generateAccessToken(user , accessTokenExpTimeMinutes);
                final String newRefreshToken;
                if(claims.getExpiration().getTime() - new Date().getTime() < ( accessTokenExpTimeMinutes + 1L)){//check if old refresh does not expired then return it instead new one // +1 because refresh token needs rest
                    newRefreshToken = jwtProvider.generateRefreshToken(user, refreshTokenExpTimeHours);
                    refreshStorage.remove(user.getLogin());
                    refreshStorage.put(user.getLogin(), newRefreshToken);
                }
                else{
                    newRefreshToken = refreshStorage.get(user.getLogin());
                }
                log.info(Auth.GET_ACCESS_TOKEN.getName() + " method ended -> status:" + HttpStatus.OK);
                return MessageResponse.response(Reason.SUCCESS_GET.getValue(), new JwtResponse(accessToken, newRefreshToken, accessTokenExpTimeMinutes * 60000), null, HttpStatus.OK);
//                return new JwtResponse(accessToken, null);
            }
        }
        log.error(Auth.GET_ACCESS_TOKEN.getName() + " method ended with invalid jwt token -> status:" + HttpStatus.UNPROCESSABLE_ENTITY);
        throw new AuthException("Invalid JWT token");
//        return new JwtResponse(null, null);
    }
}
