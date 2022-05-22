package az.abb.etaskify.service;

import az.abb.etaskify.domain.auth.User;
import az.abb.etaskify.domain.task.ChangeProgressDto;
import az.abb.etaskify.domain.task.TaskDto;
import az.abb.etaskify.domain.task.TaskProgress;
import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.TaskEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.mapper.TaskMapper;
import az.abb.etaskify.repository.TaskRepository;
import az.abb.etaskify.repository.UserRepository;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import az.abb.etaskify.service.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private final TokenService tokenService;
    private final TaskMapper taskMapper;

    private enum Task{
        ADD_METHOD("TaskService/addNewTask"),
        GET_METHOD("TaskService/getTasks"),
        GET_ALL_METHOD("TaskService/getAllTasks"),
        CHANGE_PROGRESS("TaskService/changeProgress");

        private final String methodName;

        Task(String methodName){
            this.methodName = methodName;
        }

        public String getName(){
            return methodName;
        }
    }

    private enum FieldMessage{
        USERS("users", "problem with user id(s)"),
        ORG("organization", "not fount by given token"),
        TASK_ID("taskId", "not fount"),
        INCORRECT_TASK_FOR_USER("taskId", "specified task not belong to this user");

        private final String field;
        private final String message;

        FieldMessage(String field, String message){
            this.message = message;
            this.field = field;
        }

        public String getField(){
            return field;
        }
        public String getMessage(){
            return message;
        }
    }

    public ResponseEntity<?> addNewTask(TaskDto task){
        log.info(Task.ADD_METHOD.getName() + " method started");

        List<Long> userIds;

        Optional<OrganizationEntity> organizationEntity = tokenService.getOrganizationFromToken();

        if(organizationEntity.isEmpty())
            return responseValidation(FieldMessage.ORG, Task.ADD_METHOD.getName());
        else
            userIds = userRepository.findAllIds(organizationEntity.get().getId());

        if(!CheckContains(task.getUsers(), userIds))
            return responseValidation(FieldMessage.USERS, Task.ADD_METHOD.getName());

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(task.getTitle());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setDeadLine(task.getDeadLine());
        taskEntity.setProgress(TaskProgress.getNameByOrdinal(task.getProgress()));
        System.out.println(TaskProgress.getNameByOrdinal(task.getProgress()));
        System.out.println(TaskProgress.getNameByOrdinal(task.getProgress()));
        System.out.println(TaskProgress.getNameByOrdinal(task.getProgress()));

        if(task.getUsers() != null)
            for (Long id : task.getUsers()){
                UserEntity user = new UserEntity();
                user.setId(id);

                taskEntity.addUser(user);
            }

        taskEntity.setOrganization(organizationEntity.get());

        taskRepository.save(taskEntity);
        TaskDto taskDto = taskMapper.taskToTaskDto(taskEntity);
        log.info(Task.ADD_METHOD.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_ADD.getValue(), taskDto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> getMyTasks(){
        log.info(Task.GET_METHOD.getName() + " method started");

        List<TaskDto> taskDtos = new ArrayList<>();

        Optional<UserEntity> org = tokenService.getUserFromToken();
        if(org.isPresent()){
            List<TaskEntity> tasks = new ArrayList<>(org.get().getTasks());
            taskDtos = taskMapper.tasksToTasksDto(tasks);
        }

        log.info(Task.GET_METHOD.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_GET.getValue(), taskDtos, null, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllTasks(){
        log.info(Task.GET_ALL_METHOD.getName() + " method started");

        List<TaskDto> taskDtos = new ArrayList<>();

        Optional<OrganizationEntity> org = tokenService.getOrganizationFromToken();
        if(org.isPresent()){
            List<TaskEntity> tasks = taskRepository.getAllByOrganization(org.get());
            taskDtos = taskMapper.tasksToTasksDto(tasks);
        }

        log.info(Task.GET_ALL_METHOD.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_GET.getValue(), taskDtos, null, HttpStatus.OK);
    }

    public ResponseEntity<?> changeProgress(ChangeProgressDto dto){
        log.info(Task.CHANGE_PROGRESS.getName() + " method started");

        Optional<TaskEntity> taskEntity = taskRepository.findById(dto.getTaskId());
        if(taskEntity.isEmpty())
            return responseValidation(FieldMessage.TASK_ID, Task.CHANGE_PROGRESS.getName());

        Optional<UserEntity> userEntity = tokenService.getUserFromToken();
        if(userEntity.isPresent())
            if(!taskEntity.get().getUsers().contains(userEntity.get()))
                return responseValidation(FieldMessage.INCORRECT_TASK_FOR_USER, Task.CHANGE_PROGRESS.getName());

        taskEntity.get().setProgress(TaskProgress.getNameByOrdinal(dto.getProgress()));
        taskRepository.save(taskEntity.get());
        TaskDto taskDto = taskMapper.taskToTaskDto(taskEntity.get());

        log.info(Task.CHANGE_PROGRESS.getName() + " method ended -> status:" + HttpStatus.OK);
        return MessageResponse.response(Reason.SUCCESS_GET.getValue(), taskDto, null, HttpStatus.OK);
    }

    public ResponseEntity<?> getProgresses(){
        Map<String, String> map = new HashMap<>();
        for (TaskProgress tp : TaskProgress.values()) {
            map.put(String.valueOf(tp.ordinal()),tp.getName());
        }
        return MessageResponse.response(Reason.SUCCESS_GET.getValue(), map, null, HttpStatus.OK);
    }

    private ResponseEntity<?> responseValidation(FieldMessage fm, String methodName){
        Map<String, String> map = new HashMap<>();
        map.put(fm.getField(), fm.getMessage());
        map.forEach((k, v) -> log.error(methodName + " method ended with " + k + " : " +  v + " -> status:" + HttpStatus.UNPROCESSABLE_ENTITY));
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, map, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private boolean CheckContains(List<Long> taskUserIds, List<Long> allUserIds){
        return allUserIds.containsAll(taskUserIds);
    }
}
