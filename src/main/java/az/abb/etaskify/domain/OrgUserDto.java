package az.abb.etaskify.domain;

import az.abb.etaskify.domain.auth.UserDto;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author caci
 * @since 20.05.2022
 */

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgUserDto {
    @NotNull
    @Valid
    private OrganizationDto organizationDto;

    @NotNull
    @Valid
    private AdminUserDto adminDto;
}
