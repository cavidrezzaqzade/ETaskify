package az.abb.etaskify.controller;

import az.abb.etaskify.domain.JwtRequest;
import az.abb.etaskify.domain.RefreshJwtRequest;
import az.abb.etaskify.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "the auth API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "login", description = "login", tags = {"Auth"})
    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody JwtRequest authRequest) {
        return authService.login(authRequest);
    }

    @Operation(summary = "token", description = "get token by refresh-token", tags = {"Auth"})
    @PostMapping("token")
    public ResponseEntity<?> getNewAccessToken(@Valid @RequestBody RefreshJwtRequest request) {
        return authService.getAccessToken(request.getRefreshToken());
    }
}
