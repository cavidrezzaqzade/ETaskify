package az.abb.etaskify;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@Slf4j
@OpenAPIDefinition(info = @Info(title = "ETaskify API", version = "1.0", description = "eTaskify, is a cloud based enterprise task manager platform for organizations where companies\n" +
                                                                                        "can manage their daily tasks online. eTaskify wants to hire you as a rockstar back-end\n" +
                                                                                        "developer who will help build their first Mobile App Minimum Viable Product (MVP). You’ll be\n" +
                                                                                        "expected to build the back-end of the mobile app, while the front-end will be built by other\n" +
                                                                                        "developers in the team"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
@SpringBootApplication(exclude= {UserDetailsServiceAutoConfiguration.class})
public class ETaskifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ETaskifyApplication.class, args);
    }
}
