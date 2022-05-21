package az.abb.etaskify.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author caci
 * @since 19.05.2022
 */

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organizations", schema = "public")
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "org_name", nullable = false, unique = true)
    private String orgName;

    @Column(name = "org_phone", nullable = false)
    private String orgPhone;

    @Column(name = "org_address", nullable = false)
    private String orgAddress;

    @OneToOne(
            mappedBy = "organization"/*,
            cascade = CascadeType.ALL,
            orphanRemoval = true*/
    )
    private TaskEntity task;

    @OneToOne(mappedBy = "organization")
    private UserEntity user;
}
