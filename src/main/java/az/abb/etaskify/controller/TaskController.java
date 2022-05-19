package az.abb.etaskify.controller;

import az.abb.etaskify.domain.TaskDto;
import az.abb.etaskify.domain.UserDto;
import az.abb.etaskify.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("api")
@RequiredArgsConstructor
@Tag(name = "Task", description = "the task API")
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "add task", description = "add new task", tags = {"Task"}, security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("tasks")
    public ResponseEntity<?> addNewUser(@Valid @RequestBody TaskDto task) {
        return  taskService.addNewTask(task);
    }
}
