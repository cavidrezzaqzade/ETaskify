package az.abb.etaskify.repository;

import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsernameIgnoreCase(String name);
    boolean existsByEmailIgnoreCase(String name);
    Optional<UserEntity> findByUsernameIgnoreCase(String userName);
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    List<UserEntity> getAllByOrganization(OrganizationEntity organization);

    @Query(value = "select u.id from users u where organization_id =:orgId", nativeQuery = true)
    List<Long> findAllIds(Long orgId);
}
