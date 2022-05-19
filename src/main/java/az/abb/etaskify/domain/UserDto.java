package az.abb.etaskify.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "username boş ola bilməz")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "password boş ola bilməz")
    private String password;

    private String firstname;

    private String lastname;

    private boolean status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updated;

    @NotNull(message = "rollar boş ola bilməz")
    @NotEmpty(message = "rollar boş ola bilməz")
    private List<@NotNull(message = "role id boş ola bilməz") @Min(value = 1, message = "role id 1-dən böyük olmalıdır") Long> roles;
}
