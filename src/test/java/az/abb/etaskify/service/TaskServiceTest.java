package az.abb.etaskify.service;

import az.abb.etaskify.domain.task.TaskDto;
import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.mapper.TaskMapper;
import az.abb.etaskify.repository.TaskRepository;
import az.abb.etaskify.repository.UserRepository;
import az.abb.etaskify.service.auth.TokenService;
import org.h2.util.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/**
 * @author caci
 * @since 23.05.2022
 */

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private static TaskDto taskDto;
    private static OrganizationEntity organizationEntity;
    private static Set<UserEntity> users;
    private static UserEntity userEntity;
    @BeforeAll
    static void setUpAll(){

        userEntity = UserEntity.builder()
                .id(1L)
                .username("test")
                .password("test12")
                .email("test@gmail.com")
                .build();

        users = new HashSet<>();
        users.add(userEntity);

        taskDto = TaskDto.builder().users(List.of(1L)).title("test-task").build();

        organizationEntity = OrganizationEntity.builder().id(1L).orgName("test-org").build();
    }



    @Test
    @DisplayName("check addNewTask ok")
    void givenTaskDto_WhenAddNewTask_ThenOk() {
        //given
//        given(tokenService.getOrganizationFromToken()).willReturn(Optional.of(organizationEntity));
//        given(userRepository.findAllIds(organizationEntity.getId())).willReturn(List.of(1L));

        //when
//        ResponseEntity<?> res = taskService.addNewTask(taskDto);

        //then
//        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void getMyTasks() {
    }

    @Test
    void getAllTasks() {
    }

    @Test
    void changeProgress() {
    }

    @Test
    void getProgresses() {
    }
}