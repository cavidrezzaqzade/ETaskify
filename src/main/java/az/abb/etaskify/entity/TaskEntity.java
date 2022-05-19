package az.abb.etaskify.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
@Table(name = "tasks", schema = "public")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "dead_line")
    private LocalDate deadLine;

    @Column(name = "status")
    private boolean status = false;

    @ManyToMany
    @JoinTable(name="tasks_users",
            joinColumns =
            @JoinColumn(name="task_id",referencedColumnName = "id"),
            inverseJoinColumns =
            @JoinColumn(name="user_id",referencedColumnName = "id"))
    private Set<UserEntity> users = new HashSet<>();

    public void addUser(UserEntity userEntity) {
        users.add(userEntity);
        userEntity.getTasks().add(this);
    }

    public void removeUser(UserEntity userEntity) {
        users.remove(userEntity);
        userEntity.getTasks().remove(this);

    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof TaskEntity)) return false;
        return id != null && id.equals(((TaskEntity) obj).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
