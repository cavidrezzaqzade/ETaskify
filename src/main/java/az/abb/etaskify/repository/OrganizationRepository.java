package az.abb.etaskify.repository;

import az.abb.etaskify.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author caci
 * @since 20.05.2022
 */

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {
    boolean existsByOrgNameIgnoreCase(String name);
}
