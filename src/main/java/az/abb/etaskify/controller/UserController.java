package az.abb.etaskify.controller;

import az.abb.etaskify.domain.auth.UserDto;
import az.abb.etaskify.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Tag(name = "User", description = "the user API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "add user", description = "add new user", tags = {"User"}, security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("users")
    public ResponseEntity<?> addNewUser(@Valid @RequestBody UserDto user) {
        return  userService.addNewUser(user);
    }

    @Operation(summary = "update user", description = "update the existing user", tags = {"User"}, security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("users/{id}")
    public ResponseEntity<?> updateUser(@Parameter(description = "Update an existing user in the database", required = true)
                                        @Valid @RequestBody UserDto user,
                                        @PathVariable(value = "id") Long id) {
        return userService.updateUser(user, id);
    }

    @Operation(summary = "delete user", description = "delete the existing user by id", tags = {"User"}, security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("users/{id}")
    public ResponseEntity<?> deleteUser(@Parameter(description = "delete an existing user in the database by user id", required = true)
                                        @PathVariable(value = "id") Long id) {
        return userService.deleteUser(id);
    }

    @Operation(summary = "get users", description = "get all users", tags = {"User"}, security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("users")
    public ResponseEntity<?> getUsers() {
        return userService.getUsers();
    }
}
