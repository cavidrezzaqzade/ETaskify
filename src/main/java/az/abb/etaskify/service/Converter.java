package az.abb.etaskify.service;

import az.abb.etaskify.domain.Role;
import az.abb.etaskify.domain.User;
import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class Converter {
    public Optional<User> entityToDtoLogin(UserEntity entity) {
        Optional<User> user = Optional.of(new User());

        user.get().setLogin(entity.getUsername());
        user.get().setPassword(entity.getPassword());
        user.get().setFirstName(entity.getName());
        user.get().setLastName(entity.getSurname());

        Set<Role> roles = new HashSet<>();
        for (RoleEntity roleEntity : entity.getRoles()) {
            Role r = new Role();
            r.setRoleName(roleEntity.getRoleName());
            roles.add(r);
        }
        user.get().setRoles(roles);

        return user;
    }
}
