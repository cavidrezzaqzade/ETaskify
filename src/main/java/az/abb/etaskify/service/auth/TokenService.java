package az.abb.etaskify.service.auth;

import az.abb.etaskify.domain.auth.User;
import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author caci
 * @since 22.05.2022
 */

@Component
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;

    public Optional<OrganizationEntity> getOrganizationFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName="";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = (String) authentication.getPrincipal();
        }

        Optional<UserEntity> user = userRepository.findByUsernameIgnoreCase(currentUserName);

        Optional<OrganizationEntity> organizationEntity = Optional.empty();
        
        if(user.isPresent()){
            OrganizationEntity organization = new OrganizationEntity();
            organization.setId(user.get().getOrganization().getId());
            organizationEntity = Optional.of(organization);
        }

        return organizationEntity;
    }

    public Optional<UserEntity> getUserFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName="";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = (String) authentication.getPrincipal();
        }
        return userRepository.findByUsernameIgnoreCase(currentUserName);
    }

}
