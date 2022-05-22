package az.abb.etaskify.domain.task;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author caci
 * @since 23.05.2022
 */

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProgressDto {

    @NotNull(message = "can not be null")
    private Long taskId;

    @Min(value = 0)
    @Max(value = 2)
    @NotNull(message = "can not be null")
    private Integer progress;
}
