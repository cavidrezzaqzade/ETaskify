package az.abb.etaskify.service;

import az.abb.etaskify.domain.auth.TaskDto;
import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.TaskEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.mapper.TaskMapper;
import az.abb.etaskify.repository.TaskRepository;
import az.abb.etaskify.repository.UserRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author caci
 * @since 19.05.2022
 */

@Service
@RequiredArgsConstructor
public class TaskService {
    private final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public String getUsernameFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName="";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = (String) authentication.getPrincipal();
        }
        return currentUserName;
    }

    public ResponseEntity<?> addNewTask(TaskDto task){
        log.info("TaskService/addNewTask method started");
        Map<String, String> map = new HashMap<>();

        List<Long> userIds = userRepository.findAllIds();

        if(!CheckContains(task.getUsers(), userIds))
            map.put("users", "problem with user id(s)");
        if(!map.isEmpty()){
            map.forEach((k, v) -> log.error("RoleService/addNewRole method ended with " + k + " ::: " +  v + "-> status=" + HttpStatus.UNPROCESSABLE_ENTITY));
            return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(task.getTitle());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setDeadLine(task.getDeadLine());
        taskEntity.setStatus(task.isStatus());

        if(task.getUsers() != null)
            for (Long id : task.getUsers()){
                UserEntity user = new UserEntity();
                user.setId(id);

                taskEntity.addUser(user);
            }

        Optional<UserEntity> user = userRepository.findByUsernameIgnoreCase(getUsernameFromToken());

        if(user.isPresent()){
            OrganizationEntity org = new OrganizationEntity();
            org.setId(user.get().getOrganization().getId());
            taskEntity.setOrganization(org);
        }

        taskRepository.save(taskEntity);
        TaskDto taskDto = taskMapper.taskToTaskDto(taskEntity);
        log.info("TaskService/addNewTask method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_ADD.getValue(), taskDto, null, HttpStatus.OK);
    }

    private boolean CheckContains(List<Long> taskUserIds, List<Long> allUserIds){
        return allUserIds.containsAll(taskUserIds);
    }
}
