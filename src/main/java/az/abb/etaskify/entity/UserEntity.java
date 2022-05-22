package az.abb.etaskify.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "public")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String name;

    @Column
    private String surname;

    @Column(nullable = false)
    private boolean status = true;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @ManyToMany(/*cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER*/)
    @JoinTable(name="users_roles",
            joinColumns =
            @JoinColumn(name="user_id",referencedColumnName = "id"),
            inverseJoinColumns =
            @JoinColumn(name="role_id",referencedColumnName = "id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private Set<TaskEntity> tasks = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    private OrganizationEntity organization;

    public void addRole(RoleEntity roleEntity) {
        roles.add(roleEntity);
        roleEntity.getUsers().add(this);
    }

    public void removeRole(RoleEntity roleEntity) {
        roles.remove(roleEntity);
        roleEntity.getUsers().remove(this);

    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof UserEntity)) return false;
        return id != null && id.equals(((UserEntity) obj).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
