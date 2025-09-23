package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private MessageMapper messageMapper;

  @InjectMocks
  private BasicMessageService messageService;

  private UUID userId;
  private UUID channelId;
  private UUID messageId;

  private User author;
  private Channel channel;
  private Message message;
  private MessageDto.Detail detail;
  private BinaryContent attachment;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    messageId = UUID.randomUUID();

    author = User.builder()
                 .username("author")
                 .build();

    channel = Channel.builder()
                     .name("channel")
                     .build();

    attachment = BinaryContent.builder()
                              .build();

    message = Message.builder()
                     .author(author)
                     .channel(channel)
                     .content("Hello")
                     .attachments(List.of())
                     .build();

    detail = MessageDto.Detail.builder()
                              .id(messageId)
                              .author(null)
                              .channel(null)
                              .content("Hello")
                              .build();
  }

  @Test
  void createMessage_success() {
    CreateCommand command = CreateCommand.builder()
                                         .authorId(userId)
                                         .channelId(channelId)
                                         .content("Hello")
                                         .attachments(List.of(mock(MultipartFile.class)))
                                         .build();

    given(userRepository.findById(userId)).willReturn(Optional.of(author));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    doReturn(attachment).when(binaryContentService)
                        .create(any(BinaryContentDto.CreateCommand.class));
    doReturn(message).when(messageMapper)
                     .toEntity(any(CreateCommand.class), any(Channel.class), any(User.class),
                         any());
    doReturn(detail).when(messageMapper)
                    .toDetail(any(Message.class));
    given(messageRepository.save(any(Message.class))).willReturn(message);

    MessageDto.Detail result = messageService.create(command);

    then(messageRepository).should()
                           .save(any(Message.class));
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo("Hello");
  }

  @Test
  void updateMessage_success() {

    UpdateCommand command = UpdateCommand.builder()
                                         .id(messageId)
                                         .content("Updated")
                                         .build();
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    doReturn(detail).when(messageMapper)
                    .toDetail(any(Message.class));

    MessageDto.Detail result = messageService.update(command);

    then(messageRepository).should()
                           .findById(messageId);

    assertThat(message.getContent()).isEqualTo("Updated");
    assertThat(result).isNotNull();
  }


  @Test
  void updateMessage_notFound_fail() {
    UpdateCommand command = UpdateCommand.builder()
                                         .id(messageId)
                                         .content("Updated")
                                         .build();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class, () -> messageService.update(command));
  }

  @Test
  void findMessageById_success() {
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    doReturn(detail).when(messageMapper)
                    .toDetail(any(Message.class));

    MessageDto.Detail result = messageService.findById(messageId);

    assertThat(result).isNotNull();
    then(messageRepository).should()
                           .findById(messageId);
  }

  @Test
  void findMessageById_notFound_fail() {
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class, () -> messageService.findById(messageId));
  }

  @Test
  void findAllByChannelId_noCursor_success() {
    Pageable pageable = Pageable.ofSize(10);
    List<Message> messages = List.of(message);
    given(messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable)).willReturn(
        messages);
    doReturn(detail).when(messageMapper)
                    .toDetail(any(Message.class));
    given(messageRepository.countByChannelId(channelId)).willReturn(1L);

    PageResponse<MessageDto.Detail> result = messageService.findAllByChannelId(channelId, null,
        pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void deleteMessage_success() {
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    messageService.delete(messageId);

    then(messageRepository).should()
                           .delete(message);
  }

  @Test
  void deleteMessage_notFound_fail() {
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class, () -> messageService.delete(messageId));
  }

  @Test
  void deleteAllMessages_success() {
    messageService.deleteAll();
    then(messageRepository).should()
                           .deleteAll();
  }
}
