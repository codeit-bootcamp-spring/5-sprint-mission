package com.sprint.mission.discodeit.controller;

/* 통합 테스트
 * [ ] @SpringBootTest를 활용하여 전체 ApplicationContext(전체 빈) 로드해서 테스트
 * [ ] 엔드포인트에 대한 통합 테스트 작성
 * [ ] @Transactional 활용해 독립적 실행
 * */

/* 통합 테스트 코드는 사용자가 API를 두드리듯이 컨트롤러 엔드포인트에
 *  직접 HTTP 요청을 보내는 것이 목적이기 때문에, Service 주입 안함
 *  <-> Repository는 DB 검증용으로 주입하는 것임
 * */


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") //application-test.yaml
@Transactional // 각 테스트마다 DB 롤백
public class UserApiIntegrationTest {

  @Autowired
  TestRestTemplate restTemplate; //진짜 서버처럼 HTTP로 API를 테스트하는 도구

  @Autowired
  UserRepository userRepository; // DB 저장 되는지 확인용

  // 회원가입 성공
  @Test
  void createUser_success() {

    //given
    UserDto req = new UserDto();
    req.setUsername("test");
    req.setEmail("test@test.com");
    req.setPassword("test123456");

    //when
    ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", req, UserDto.class);

    //then: 상태코드/데이터 검증
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().getUsername()).isEqualTo("test");

    //DB 저장 확인
    assertThat(userRepository.existsByUsername("test")).isTrue();
  }


  //--- 실패: 회원가입 실패 테스트 (중복 username)
  @Test
  void createUser_fail_duplicateUsername() {

    //given: 이미 같은 username의 User 저장
    User user = new User("test", "pw123", "test@test.com");
    userRepository.save(user);

    //when: 회원가입 시도 (username 중복)
    ResponseEntity<String> response = restTemplate.postForEntity("/api/users", user, String.class);

    //then: 에러 상태코드 기대
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  //--- 생성: 목록조회 성공 테스트 ---
  @Test
  void getUserList_success() {

    //given
    userRepository.save(new User("test1", "pw1", "test1@test.com"));
    userRepository.save(new User("test2", "pw2", "test2@test.com"));

    //when
    ResponseEntity<UserDto[]> response = restTemplate.getForEntity("/api/users", UserDto[].class);

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  //--삭제: 성공 --
  @Test
  void deleteUser_success() {
    User user = userRepository.save(new User("test12", "pw", "test1@test.com"));
    UUID id = user.getId();

    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + id, HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(userRepository.existsById(id)).isFalse();
  }


}
