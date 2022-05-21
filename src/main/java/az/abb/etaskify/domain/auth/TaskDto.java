package az.abb.etaskify.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class TaskDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "title can not be empty")
    private String title;

    @NotBlank(message = "description can not be empty")
    private String description;

    private LocalDate deadLine;

    private boolean status;

    @NotNull(message = "users can not be empty")
    @NotEmpty(message = "users can not be empty")
    private List<@NotNull(message = "user id can not be empty") @Min(value = 1, message = "user id must be greater than zero") Long> users;
}
