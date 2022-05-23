package az.abb.etaskify.repository;

import az.abb.etaskify.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    /**
     * -> We can also say a Integration test
     * -> Because jpa repositories inferred and does not need to be unit tested
     * -> Components will be automatically configured to point to an embedded,
     *    in-memory database instead of the “real” database we might have configured
     *    in application.properties or application.yml files.
     * -> Note that by default the application context containing all these components,
     *    including the in-memory database, is shared between all test methods within all @DataJpaTest-annotated test classes.
     *    This is why, by default, each test method runs in its own transaction,
     *    which is rolled back after the method has executed. This way, the database
     *    state stays pristine between tests and the tests stay independent of each other.
     *
     *    links: -> https://reflectoring.io/spring-boot-data-jpa-test/
     *           -> https://stackoverflow.com/questions/23435937/how-to-test-spring-data-repositories
     */

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUpAll() {
        UserEntity user = UserEntity.builder()
                .username("cavid")
                .password("12345")
                .name("c")
                .surname("r")
                .email("example@gmail.com")
                .build();

        userRepository.save(user);
    }

    @DisplayName("check exists by upper username")
    @Test
    void givenUpperUsername_WhenExists_ThenTrue() {
        boolean checkIfExists = userRepository.existsByUsernameIgnoreCase("CAViD");//I
        assertThat(checkIfExists).isTrue();
    }

    @DisplayName("check exists by lower username")
    @Test
    void givenLowerUsername_WhenExists_ThenTrue() {
        boolean checkIfExists = userRepository.existsByUsernameIgnoreCase("cavid");
        assertThat(checkIfExists).isTrue();
    }

    @DisplayName("check does not exist by upper username")
    @Test
    void givenUpperUsername_WhenDoesNotExist_ThenFalse() {
        boolean checkIfExists = userRepository.existsByUsernameIgnoreCase("CavaD");
        assertThat(checkIfExists).isFalse();
    }

    @DisplayName("check does not exist by lower username")
    @Test
    void givenLowerUsername_WhenDoesNotExist_ThenFalse() {
        boolean checkIfExists = userRepository.existsByUsernameIgnoreCase("cavad");
        assertThat(checkIfExists).isFalse();
    }

    //////////////////////////////

    @DisplayName("check finds by username")
    @Test
    void givenLowerUsername_WhenFinds_ThenTrue() {
        Optional<UserEntity> user = userRepository.findByUsernameIgnoreCase("cavid");
        assertTrue(user.isPresent());
    }

    @DisplayName("check finds by username")
    @Test
    void givenUpperUsername_WhenFinds_ThenTrue() {
        Optional<UserEntity> user = userRepository.findByUsernameIgnoreCase("CAvid");
        assertTrue(user.isPresent());
    }

    @DisplayName("check does not find by username")
    @Test
    void givenLowerUsername_WhenDoesNotFind_ThenFalse() {
        Optional<UserEntity> user = userRepository.findByUsernameIgnoreCase("cavad");
        assertTrue(user.isEmpty());
    }

    @DisplayName("check does not find by username")
    @Test
    void givenUpperUsername_WhenDoesNotFind_ThenFalse() {
        Optional<UserEntity> user = userRepository.findByUsernameIgnoreCase("caVad");
        assertTrue(user.isEmpty());
    }

}