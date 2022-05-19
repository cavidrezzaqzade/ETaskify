package az.abb.etaskify.domain;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class JwtRequest {
    @NotEmpty(message = "username cannot be null")
    private String login;

    @NotEmpty(message = "password cannot be null")
    private String password;
}
