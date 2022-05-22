package az.abb.etaskify.repository;

import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.TaskEntity;
import az.abb.etaskify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
 * @author caci
 * @since 20.05.2022
 */

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
//    List<TaskEntity> getAllByOrganizationAndUsers(OrganizationEntity organization, Set<UserEntity> users);
    List<TaskEntity> getAllByOrganization(OrganizationEntity organization);
}
