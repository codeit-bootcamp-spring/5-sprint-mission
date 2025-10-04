package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") //application-test.yaml
@Transactional // 각 테스트마다 DB 롤백
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //테스트마다 독립된 컨텍스트
public class UserApiIntegrationTest {

  /* 통합 테스트
   * [ ] @SpringBootTest를 활용하여 전체 ApplicationContext(전체 빈) 로드해서 테스트
   * [ ] 엔드포인트에 대한 통합 테스트 작성
   * [ ] @Transactional 활용해 독립적 실행
   * */

  /* 통합 테스트 코드: 사용자가 API를 두드리듯이 컨트롤러 엔드포인트에
   *  직접 HTTP 요청을 보내는 것이 목적이기 때문에, Service 주입X
   *  <-> Repository는 DB 검증용으로 주입
   * */

  @Autowired
  TestRestTemplate restTemplate; //진짜 서버처럼 HTTP로 API를 테스트하는 도구

  @Autowired
  UserRepository userRepository; // DB 저장 되는지 확인용

  /* API는 UserDto로 JSON을 받으니까
   * 테스트에서도 UserDto를 만들어야 함
   */

  @Test
  @DisplayName("회원가입 성공")
  void createUser_success() {

    //given
    UserDto req1 = new UserDto();
    req1.setUsername("test");
    req1.setEmail("test@test.com");
    req1.setPassword("test123456");

    UserDto req2 = new UserDto();
    req2.setUsername("test1");
    req2.setEmail("test1@test.com");
    req2.setPassword("test123456");

    //회원가입 두명
    restTemplate.postForEntity("/api/users", req1, UserDto.class);
    restTemplate.postForEntity("/api/users", req2, UserDto.class);

    //when - 목록 조회
    ResponseEntity<UserDto[]> response = restTemplate.getForEntity("/api/users", UserDto[].class);

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  @DisplayName("회원가입실패 - 중복 username")
  void createUser_fail_duplicateUsername() {

    //given: 가입 성공
    UserDto req = new UserDto();
    req.setUsername("haram");
    req.setEmail("hara@test.com");
    req.setPassword("hara123456");
    restTemplate.postForEntity("/api/users", req, UserDto.class);

    //when: 중복 이름으로 다시 시도
    UserDto dupReq = new UserDto();
    dupReq.setUsername("haram");
    dupReq.setEmail("another@test.com");
    dupReq.setPassword("test123456");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/users", dupReq,
        String.class);

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void deleteUser_success() {

    //given
    UserDto req = new UserDto();
    req.setUsername("haram");
    req.setEmail("another@test.com");
    req.setPassword("test123456");

    ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", req, UserDto.class);
    UUID id = response.getBody().getId();

    //when
    ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/users/" + id,
        HttpMethod.DELETE, null, Void.class);

    //then
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(userRepository.existsById(id)).isFalse();

  }

  @Test
  @DisplayName("유저 삭제 실패 - 존재하지 않는 ID")
  void deleteUser_fail_notFound() {

    //given: DB에 없는 랜덤 UUID 생성
    UUID notExistId = UUID.randomUUID();

    //when: 없는 ID로 삭제 시도
    ResponseEntity<Void> response = restTemplate.exchange("/api/users/" + notExistId,
        HttpMethod.DELETE, null, Void.class);

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}