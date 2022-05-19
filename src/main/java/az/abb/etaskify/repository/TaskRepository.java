package az.abb.etaskify.repository;

import az.abb.etaskify.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author caci
 * @since 19.05.2022
 */

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

}
