package az.abb.etaskify.domain.jwt;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class JwtRequest {
    @NotEmpty(message = "cannot be null")
    private String username;

    @NotEmpty(message = "cannot be null")
    private String password;
}
