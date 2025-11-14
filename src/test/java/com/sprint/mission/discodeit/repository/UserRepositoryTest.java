package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = User.builder()
               .username("testuser")
               .email("test@example.com")
               .password("pass")
               .role("USER")
               .build();
    userRepository.save(user);
  }

  @Test
  void findByUsername_success() {
    Optional<User> found = userRepository.findByUsername("testuser");
    assertThat(found).isPresent();
    assertThat(found.get()
                    .getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void findByUsername_notFound() {
    Optional<User> found = userRepository.findByUsername("notexist");
    assertThat(found).isEmpty();
  }

  @Test
  void existsByUsername_true() {
    boolean exists = userRepository.existsByUsername("testuser");
    assertThat(exists).isTrue();
  }

  @Test
  void existsByUsername_false() {
    boolean exists = userRepository.existsByUsername("notexist");
    assertThat(exists).isFalse();
  }

  @Test
  void existsByEmail_true() {
    boolean exists = userRepository.existsByEmail("test@example.com");
    assertThat(exists).isTrue();
  }

  @Test
  void existsByEmail_false() {
    boolean exists = userRepository.existsByEmail("notexist@example.com");
    assertThat(exists).isFalse();
  }

  @Test
  void findById_success() {
    Optional<User> found = userRepository.findById(user.getId());
    assertThat(found).isPresent();
    assertThat(found.get()
                    .getUsername()).isEqualTo("testuser");
  }

  @Test
  void findById_notFound() {
    Optional<User> found = userRepository.findById(java.util.UUID.randomUUID());
    assertThat(found).isEmpty();
  }
}
