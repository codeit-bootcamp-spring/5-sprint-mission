// src/test/java/com/sprint/mission/discodeit/repository/UserRepositoryTest.java
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import static org.assertj.core.api.Assertions.*;

class UserRepositoryTest extends RepositorySliceTestBase {

  @Autowired UserRepository userRepository;

  @Test
  void existsByEmail_true_whenSaved() {
    var u = new User("neo","neo@matrix.io","pw", null);
    userRepository.save(u);

    assertThat(userRepository.existsByEmail("neo@matrix.io")).isTrue();
  }

  @Test
  void existsByUsername_false_whenNotSaved() {
    assertThat(userRepository.existsByUsername("unknown")).isFalse();
  }

  @Test
  void findByUsername_success() {
    var u = new User("trinity","t@x.io","pw", null);
    userRepository.save(u);

    var found = userRepository.findByUsername("trinity");
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("t@x.io");
  }

  @Test
  void findAllWithProfileAndStatus_fetchesAssociations() {
    var profile = new BinaryContent("p.jpg", 10L, "image/jpeg");
    var u = new User("morpheus","m@x.io","pw", profile);
    userRepository.save(u);

    var status = new UserStatus(u, Instant.now());

    var all = userRepository.findAllWithProfileAndStatus();
    assertThat(all).isNotEmpty();
  }
}
