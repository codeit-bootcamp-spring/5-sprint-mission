package com.sprint.mission.discodeit.repository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

/* [ ] DataJpaTest 활용
 * [ ] 데이터소스는 H2 인메모리 데이터 베이스를 사용하고, PostgreSQL 호환 모드로 설정
 * [ ] JPA Audit 기능을 활성화 하기 위해 테스트 클래스에 @EnableJpaAuditing을 추가
 * [ ] 커스텀 쿼리 메소드
 * [ ] 페이징 및 정렬 메소드
 */

@DataJpaTest // 슬라이스 테스트
@ActiveProfiles("test") // application-test.yaml 적용
@EnableJpaAuditing
public class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  /* 유저 이름찾기 성공
   */
  @Test
  void existsByUsername_success() {
    // given
    userRepository.save(new User("user1", "pw1", "user1@email.com"));
    // when & then
    assertThat(userRepository.existsByUsername("user1")).isTrue();
  }

  /* 유저 이름찾기 실패
   */
  @Test
  void existsByUsername_fail() {
    assertThat(userRepository.existsByUsername("nouser")).isFalse();
  }

  /* 유저 이메일찾기 성공
   */
  @Test
  void existsByEmail_success() {
    userRepository.save(new User("user2", "pw2", "user2@email.com"));
    assertThat(userRepository.existsByEmail("user2@email.com")).isTrue();
  }

  /* 유저 이메일찾기 실패
   */
  @Test
  void existsByEmail_fail() {
    assertThat(userRepository.existsByEmail("notfound@email.com")).isFalse();
  }

  /* 유저 이름&비번찾기 성공
   */
  @Test
  void findByUsernameAndPassword_success() {
    // given
    userRepository.save(new User("user3", "pw3", "user3@email.com"));
    // when
    Optional<User> found = userRepository.findByUsernameAndPassword("user3", "pw3");
    // then
    assertThat(found).isPresent();
    assertThat(found.get().getUsername()).isEqualTo("user3");
  }

  /* 유저 비번찾기 실패
   */
  @Test
  void findByUsernameAndPassword_fail_wrongPassword() {
    userRepository.save(new User("user4", "pw4", "user4@email.com"));
    Optional<User> found = userRepository.findByUsernameAndPassword("user4", "wrongpw");
    assertThat(found).isEmpty();
  }

  /* 유저 이름찾기 실패
   */
  @Test
  void findByUsernameAndPassword_fail_noUser() {
    Optional<User> found = userRepository.findByUsernameAndPassword("noid", "nopw");
    assertThat(found).isEmpty();
  }
}

