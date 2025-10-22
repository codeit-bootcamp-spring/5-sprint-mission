package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MessageApiIntegrationTest {

  @Autowired
  TestRestTemplate restTemplate;

  @Autowired
  MessageRepository messageRepository;

  @Autowired
  ChannelRepository channelRepository;

  //유저 먼저 생성
  private UUID createTestUser() {
    UserDto user = new UserDto();
    user.setUsername("haram");
    user.setEmail("haram@test.com");
    user.setPassword("12345678");
    ResponseEntity<UserDto> userRes = restTemplate.postForEntity("/api/users", user, UserDto.class);
    return userRes.getBody().getId();
  }

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_success() {
    //given: 유저 및 채널 생성
    UUID authorId = createTestUser();

    ChannelDto channelDto = new ChannelDto();
    channelDto.setName("channel1");
    channelDto.setDescription("설명");
    channelDto.setChannelType(ChannelType.PUBLIC);

    restTemplate.postForEntity("/api/channels/public", channelDto, Void.class);
    UUID channelId = channelRepository.findAll().get(0).getId();

    //when
    MessageDto req = new MessageDto();
    req.setContent("안녕하세요");
    req.setChannelId(channelId);
    req.setAuthorId(authorId);

    ResponseEntity<MessageDto> res =
        restTemplate.postForEntity("/api/messages/simple", req, MessageDto.class);

    // then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(res.getBody().getContent()).isEqualTo("안녕하세요");
    assertThat(res.getBody().getChannelId()).isEqualTo(channelId);
  }

  @Test
  @DisplayName("채널별 메시지 조회 성공")
  void findAllByChannelId_success() {
    //given
    UUID authorId = createTestUser(); // 🟢 추가

    ChannelDto channelDto = new ChannelDto();
    channelDto.setName("channel2");
    channelDto.setDescription("설명2");
    channelDto.setChannelType(ChannelType.PUBLIC);

    restTemplate.postForEntity("/api/channels/public", channelDto, Void.class);
    UUID channelId = channelRepository.findAll().get(0).getId();

    //메시지 두 개 생성 (authorId 포함)
    MessageDto msg1 = new MessageDto();
    msg1.setContent("첫번째");
    msg1.setChannelId(channelId);
    msg1.setAuthorId(authorId); // 🟢 추가
    restTemplate.postForEntity("/api/messages/simple", msg1, MessageDto.class);

    MessageDto msg2 = new MessageDto();
    msg2.setContent("두번째");
    msg2.setChannelId(channelId);
    msg2.setAuthorId(authorId); // 🟢 추가
    restTemplate.postForEntity("/api/messages/simple", msg2, MessageDto.class);

    // when
    ResponseEntity<MessageDto[]> res = restTemplate.getForEntity(
        "/api/messages?channelId=" + channelId, MessageDto[].class);

    // then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).hasSize(2);
  }
}
