package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager em;

  private User createUser(String username, String email) {
    User user = new User(username, email, "password1234", null);
    UserStatus userStatus = new UserStatus(user, Instant.now());
    return user;
  }

  @Test
  @DisplayName("findByUsername")
  void findByUsernameTest() {
    String username = "codeit";
    User user = createUser(username, "codeit@email.com");
    userRepository.save(user);

    em.flush();
    em.clear();

    Optional<User> result = userRepository.findByUsername(username);
    Assertions.assertThat(result).isPresent();
    Assertions.assertThat(result.get().getUsername()).isEqualTo("codeit");
    Assertions.assertThat(result.get().getEmail()).isEqualTo("codeitemail.com");
  }

}
