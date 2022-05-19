package az.abb.etaskify;

import az.abb.etaskify.entity.RoleEntity;
import az.abb.etaskify.entity.UserEntity;
import az.abb.etaskify.repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Set;

@Slf4j
@OpenAPIDefinition(info = @Info(title = "ETaskify API", version = "1.0", description = "eTaskify, is a cloud based enterprise task manager platform for organizations where companies\n" +
                                                                                        "can manage their daily tasks online. eTaskify wants to hire you as a rockstar back-end\n" +
                                                                                        "developer who will help build their first Mobile App Minimum Viable Product (MVP). You’ll be\n" +
                                                                                        "expected to build the back-end of the mobile app, while the front-end will be built by other\n" +
                                                                                        "developers in the team"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
@SpringBootApplication(exclude= {UserDetailsServiceAutoConfiguration.class})
public class BookAppAuthApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(BookAppAuthApplication.class, args);

        //startup time initial data inserting to database as admin user
        UserRepository user = configurableApplicationContext.getBean(UserRepository.class);
        UserEntity userEntity = UserEntity.builder()
                .username("cavid")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .name("cavid")
                .surname("rezzaqzade")
                .status(true)
                .roles(Set.of(RoleEntity.builder().roleName("ADMIN").status(true).build()))
                .build();

        try{ user.save(userEntity); }
        catch (Exception e){ log.error("In main save default user: " + e.getMessage()); }
    }

}
