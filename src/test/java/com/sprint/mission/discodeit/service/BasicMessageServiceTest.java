package com.sprint.mission.discodeit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {

  // MessageService: create, update, delete, findByChannelId

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageMapper messageMapper;

  @InjectMocks
  private BasicMessageService messageService;

  private UUID messageId;
  private UUID channelId;
  private UUID authorId;
  private UUID attachmentId;
  private String content;
  private Message message;
  private MessageDto messageDto;
  private Channel channel;
  private User author;
  private BinaryContent attachment;
  private BinaryContentDto attachmentDto;

  @BeforeEach
  void setUp() {
    messageId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    authorId = UUID.randomUUID();
    attachmentId = UUID.randomUUID();
    content = "테스트";

    channel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다");

    author = new User("testUser", "test01@email.com", "test1234", null);
    attachment = new BinaryContent("test.txt", 100L, "text/plain");
    attachmentDto = new BinaryContentDto(attachmentId, "test.txt", 100L, "text/plain");

    message = new Message(content, channel, author, List.of(attachment));
    messageDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        content,
        channelId,
        new UserDto(authorId, "testUser", "test01@email.com", null, true),
        List.of(attachmentDto)
    );
  }

  @Test
  @DisplayName("메세지 생성 테스트")
  void createMessage() {
    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    BinaryContentCreateRequest bRequest = new BinaryContentCreateRequest("test.txt", "text/plain",
        new byte[100]);
    List<BinaryContentCreateRequest> attachments = List.of(bRequest);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));

    given(messageRepository.save(any())).willReturn(message);
    given(messageMapper.toDto(any())).willReturn(messageDto);

    MessageDto result = messageService.create(request, attachments);

    Assertions.assertThat(result).isEqualTo(messageDto);
  }

  @Test
  @DisplayName("메세지 전송 실패 테스트")
  void create_Message_fail() {
    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("메세지 수정 테스트")
  void updateMessage() {
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(any())).willReturn(messageDto);

    MessageDto result = messageService.update(messageId, request);

    Assertions.assertThat(result).isEqualTo(messageDto);
  }

  @Test
  @DisplayName("메세지 수정 테스트 실패")
  void update_Message_fail() {
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("메세지 삭제 테스트")
  void deleteMessage() {
    given(messageRepository.existsById(messageId)).willReturn(false);

    Assertions.assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

}
