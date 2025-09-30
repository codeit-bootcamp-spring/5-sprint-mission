package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.impl.MessageServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

/* 단위 테스트
 * 서비스 레이어의 주요 메소드에 대한 단위 테스트
 * DB, 외부 API 연결하지 않고 Mock을 사용해 검증
 */

class MessageServiceImplTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private MessageMapper messageMapper;

  @InjectMocks
  private MessageServiceImpl messageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // CREATE 성공
  @Test
  void createMessage_success() {
    UUID authorId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    MessageDto dto = new MessageDto();
    dto.setContent("hello");
    dto.setAuthorId(authorId);
    dto.setChannelId(channelId);

    User user = new User();
    Channel channel = new Channel();
    Message message = new Message();
    Message saved = new Message();

    given(userRepository.findById(authorId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(messageMapper.toEntityForCreate(dto, user, channel)).willReturn(message);
    given(messageRepository.save(message)).willReturn(saved);
    given(messageMapper.toDto(saved)).willReturn(dto);

    MessageDto result = messageService.create(dto, null);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo("hello");
  }

  /* CREATE 실패
   * 유저 없음
   */
  @Test
  void createMessage_fail_userNotFound() {
    MessageDto dto = new MessageDto();
    UUID authorId = UUID.randomUUID();
    dto.setAuthorId(authorId);

    given(userRepository.findById(authorId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(dto, null))
        .isInstanceOf(UserNotFoundException.class);
  }

  /* CREATE 실패
   * 채널 없음
   */
  @Test
  void createMessage_fail_channelNotFound() {
    MessageDto dto = new MessageDto();
    UUID authorId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    dto.setAuthorId(authorId);
    dto.setChannelId(channelId);

    given(userRepository.findById(authorId)).willReturn(Optional.of(new User()));
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(dto, null))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  /*UPDATE 성공
   */
  @Test
  void updateMessage_success() {
    UUID messageId = UUID.randomUUID();
    Message message = new Message();
    MessageDto dto = new MessageDto();
    dto.setNewContent("new msg");

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    // updateEntityFromDto는 void 메서드이므로 stubbing 생략 가능
    given(messageMapper.toDto(message)).willReturn(dto);

    MessageDto result = messageService.update(messageId, dto);

    assertThat(result.getNewContent()).isEqualTo("new msg");
  }

  /* UPDATE 실패
   * 메시지 없음
   */
  @Test
  void updateMessage_fail_messageNotFound() {
    UUID messageId = UUID.randomUUID();
    MessageDto dto = new MessageDto();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(messageId, dto))
        .isInstanceOf(MessageNotFoundException.class);
  }

  /* DELETE 성공
   */
  @Test
  void deleteMessage_success() {
    UUID messageId = UUID.randomUUID();
    Message message = new Message();
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    willDoNothing().given(messageRepository).delete(message);

    messageService.delete(messageId);

    then(messageRepository).should().delete(message);
  }

  /* DELETE 실패
   * 메시지 없음
   */
  @Test
  void deleteMessage_fail_messageNotFound() {
    UUID messageId = UUID.randomUUID();
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  /* findAllByChannelId 성공
   */

  @Test
  void findAllByChannelId_success() {
    UUID channelId = UUID.randomUUID();

    // 1. 엔티티 생성자만 활용해서 채널 만들기
    Channel channel = new Channel("채널명", "설명", ChannelType.PUBLIC);
    ReflectionTestUtils.setField(channel, "id", channelId); // id 강제 할당

    // 2. 메시지도 생성자만 사용해서 채널 연결
    Message msg1 = new Message("hello", null, channel);
    Message msg2 = new Message("hi", null, channel);

    List<Message> messageList = Arrays.asList(msg1, msg2);

    // 3. DTO도 생성자나 직접 필드할당(여긴 setter 상관 없음)
    MessageDto dto1 = new MessageDto();
    dto1.setContent("hello");
    MessageDto dto2 = new MessageDto();
    dto2.setContent("hi");

    // 4. Mock 세팅
    given(messageRepository.findAll()).willReturn(messageList);
    given(messageMapper.toDto(msg1)).willReturn(dto1);
    given(messageMapper.toDto(msg2)).willReturn(dto2);

    // when
    List<MessageDto> result = messageService.findAllByChannelId(channelId);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getContent()).isEqualTo("hello");
    assertThat(result.get(1).getContent()).isEqualTo("hi");
  }
}