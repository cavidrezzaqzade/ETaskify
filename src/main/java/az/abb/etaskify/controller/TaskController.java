package az.abb.etaskify.controller;

import az.abb.etaskify.domain.task.ChangeProgressDto;
import az.abb.etaskify.domain.task.TaskDto;
import az.abb.etaskify.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "get my tasks", description = "get my tasks", tags = {"Task"}, security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("tasks")
    public ResponseEntity<?> getMyTasks() {
        return  taskService.getMyTasks();
    }

    @Operation(summary = "get all tasks", description = "get all tasks for admins", tags = {"Task"}, security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("allTasks")
    public ResponseEntity<?> getAllTasks() {
        return  taskService.getAllTasks();
    }

    @Operation(summary = "get progresses", description = "get all progresses", tags = {"Task"}, security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("progress")
    public ResponseEntity<?> getProgresses() {
        return  taskService.getProgresses();
    }

    @Operation(summary = "change progress", description = "change your task progress", tags = {"Task"}, security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("progress")
    public ResponseEntity<?> changeTaksProgress(@Valid @RequestBody ChangeProgressDto task) {
        return  taskService.changeProgress(task);
    }

}
