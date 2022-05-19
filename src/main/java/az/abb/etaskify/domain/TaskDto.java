package az.abb.etaskify.domain;

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

    @NotBlank(message = "title boş ola bilməz")
    private String title;

    @NotBlank(message = "description boş ola bilməz")
    private String description;

    @NotNull(message = "deadline boş ola bilməz")
    private LocalDate deadLine;

    private boolean status;

    @NotNull(message = "users boş ola bilməz")
    @NotEmpty(message = "users boş ola bilməz")
    private List<@NotNull(message = "role id boş ola bilməz") @Min(value = 1, message = "user id 1-dən böyük olmalıdır") Long> users;
}
