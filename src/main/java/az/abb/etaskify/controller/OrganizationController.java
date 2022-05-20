package az.abb.etaskify.controller;

import az.abb.etaskify.domain.OrgUserDto;
import az.abb.etaskify.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author caci
 * @since 19.05.2022
 */

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Signup", description = "the Signup API")
public class OrganizationController {

    private final OrganizationService service;

    @Operation(summary = "add organization and admin user", description = "add organization and default admin user", tags = {"Signup"})
    @PostMapping("orgs")
    public ResponseEntity<?> addOrganization(@Valid @RequestBody OrgUserDto dto) {
        return service.addNewOrg(dto);
    }

}
