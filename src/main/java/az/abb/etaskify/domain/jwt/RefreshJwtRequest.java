package az.abb.etaskify.domain.jwt;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RefreshJwtRequest {
    @NotBlank(message = "can not be empty")
    public String refreshToken;
}
