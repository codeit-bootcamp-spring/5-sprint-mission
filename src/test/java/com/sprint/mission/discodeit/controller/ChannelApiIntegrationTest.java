package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") //application.yaml
@Transactional //각 테스트마다 롤백
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //테스트마다 독립된 컨텍스트
public class ChannelApiIntegrationTest {

  /* 통합 테스트
   * @SpringBootTest를 활용하여 전체 ApplicationContext 로드해서 테스트
   * 엔드 포인트에 대한 통합 테스트 작성
   * @Transactionl 활용해 독립적 실행
   * */

  /* 사용자가 API를 두드리듯이 컨트롤러 엔드 포인트에
   * 직접 HTTP 요청을 보내는 것이 목적이기 때문에, Service 주입X
   * <-> Repository는 DB 검증용으로 주입
   */

  @Autowired
  TestRestTemplate restTemplate; //진짜 서버처럼 HTTP로 API를 테스트하는 도구

  @Autowired
  ChannelRepository channelRepository; // DB 저장되는지 확인용

  /* API는 ChannelDTo로 JSON을 받으니까
   * 테스트에서도 ChannelDto를 만들어야 함
   */

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublicChannel_success() {

    //given
    ChannelDto req = new ChannelDto();
    req.setName("테스트채널");
    req.setDescription("설명");
    req.setChannelType(ChannelType.PUBLIC);

    //when
    ResponseEntity<Void> response = restTemplate.postForEntity("/api/channels/public", req,
        Void.class); //응답 파싱X

    //then
    var all = channelRepository.findAll();
    assertThat(all).hasSize(1);
    assertThat(all.get(0).getName()).isEqualTo("테스트채널");
    assertThat(all.get(0).getDescription()).isEqualTo("설명");
    assertThat(all.get(0).getChannelType()).isEqualTo(ChannelType.PUBLIC);

  }

  @Test
  @DisplayName("채널 수정 성공")
  void updateChannel_success() throws JsonProcessingException {

    /*채널 생성 먼저
     */
    //given
    ChannelDto req = new ChannelDto();
    req.setName("수정전");
    req.setDescription("수정전 설명");
    req.setChannelType(ChannelType.PUBLIC);

    restTemplate.postForEntity("/api/channels/public", req, Void.class);
    UUID id = channelRepository.findAll().get(0).getId();

    // when: 수정할 값 (@Valid 필수 꼭 넣기)
    ChannelDto updateDto = new ChannelDto();
    updateDto.setName("수정전");
    updateDto.setDescription("수정전 설명");
    updateDto.setChannelType(ChannelType.PUBLIC);
    updateDto.setNewName("수정후");
    updateDto.setNewDescription("수정후 설명");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON); //요청 헤더 지정
    HttpEntity<ChannelDto> entity = new HttpEntity<>(updateDto, headers); //dto,header 묶기
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/channels/" + id, HttpMethod.PATCH, entity, Void.class);

    //then
    restTemplate.exchange("/api/channels/" + id, HttpMethod.PATCH, entity, Void.class); //patch 요청

    //다시 API로 조회(DB에서 최신 상태 받아옴)
    ResponseEntity<ChannelDto> result = restTemplate.getForEntity("/api/channels/" + id,
        ChannelDto.class);

    assertThat(result.getBody().getName()).isEqualTo("수정후");
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_success() {
// given: 채널 생성
    ChannelDto req = new ChannelDto();
    req.setName("삭제채널");
    req.setDescription("삭제용 설명");
    req.setChannelType(ChannelType.PUBLIC);

    restTemplate.postForEntity("/api/channels/public", req, Void.class);
    UUID id = channelRepository.findAll().get(0).getId();

    // when: 삭제
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/channels/" + id, HttpMethod.DELETE, null, Void.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(channelRepository.existsById(id)).isFalse();
  }
}

