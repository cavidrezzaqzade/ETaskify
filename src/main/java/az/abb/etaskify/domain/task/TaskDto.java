package az.abb.etaskify.domain.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;
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

    @NotBlank(message = "can not be empty")
    private String title;

    @NotBlank(message = "can not be empty")
    private String description;

    private LocalDate deadLine;

    @Min(value = 0, message = "progress must be between 0-2")
    @Max(value = 2, message = "progress must be between 0-2")
    private Integer progress;

    @NotNull(message = "can not be empty")
    @NotEmpty(message = "can not be empty")
    private List<@NotNull(message = "user id can not be empty") @Min(value = 1, message = "user id must be greater than zero") Long> users;
}
