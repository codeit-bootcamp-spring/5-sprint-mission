// src/test/java/com/sprint/mission/discodeit/repository/UserStatusRepositoryTest.java
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class UserStatusRepositoryTest extends RepositorySliceTestBase {

  @Autowired UserStatusRepository userStatusRepository;
  @Autowired UserRepository userRepository;

  @Test
  void findByUserId_success() {
    var u = userRepository.save(new User("neo","n@x.io","pw", null));
    var s = userStatusRepository.save(new UserStatus(u, Instant.now()));

    var found = userStatusRepository.findByUserId(u.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(s.getId());
  }

  @Test
  void findByUserId_empty_whenNone() {
    var u = userRepository.save(new User("trinity","t@x.io","pw", null));
    var found = userStatusRepository.findByUserId(u.getId());
    assertThat(found).isEmpty();
  }
}
