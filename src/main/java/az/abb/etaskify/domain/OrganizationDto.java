package az.abb.etaskify.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author caci
 * @since 19.05.2022
 */

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "orgName boş ola bilməz")
    private String orgName;

    @NotBlank(message = "orgPhone boş ola bilməz")
    private String orgPhone;

    @NotBlank(message = "orgAddress boş ola bilməz")
    private String orgAddress;


}
