package az.abb.etaskify.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotEmpty(message = "role boş ola bilməz")
//    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private String roleName;

    private boolean status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updated;
}
