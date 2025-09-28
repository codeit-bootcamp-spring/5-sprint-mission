package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock private MessageRepository messageRepository;
  @Mock private ChannelRepository channelRepository;
  @Mock private UserRepository userRepository;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private BinaryContentStorage binaryContentStorage;
  @Mock private MessageMapper messageMapper;
  @Mock private PageResponseMapper pageResponseMapper;

  @InjectMocks private BasicMessageService messageService;

  private Channel channel;
  private User author;
  private Message message;
  private MessageDto messageDto;
  private Pageable pageable;

  private BinaryContent file;
  private BinaryContentDto fileDto;

  @BeforeEach
  void setUp() {
    channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
    ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());

    author = new User("u", "u@test.com", "12341234", null);
    ReflectionTestUtils.setField(author, "id", UUID.randomUUID());

    message = new Message("hello", channel, author, List.of());
    ReflectionTestUtils.setField(message, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(message, "createdAt", Instant.now());
    ReflectionTestUtils.setField(message, "updatedAt", Instant.now());

    var authorDto = new UserDto(author.getId(), author.getUsername(), author.getEmail(), null, false);
    messageDto = new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        channel.getId(),
        authorDto,
        List.of()
    );

    file = new BinaryContent("a.txt", 1L, "text/plain");
    ReflectionTestUtils.setField(file, "id", UUID.randomUUID());
    fileDto = new BinaryContentDto(file.getId(), "a.txt", 1L, "text/plain");

    pageable = PageRequest.of(0, 2);
  }

  // ---------- create ----------

  @Test
  @DisplayName("create - 첨부 없음")
  void create_withoutAttachments() {
    var req = new MessageCreateRequest(channel.getId(), author.getId(), "hello");

    given(userRepository.findById(author.getId())).willReturn(Optional.of(author));
    given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(message)).willReturn(messageDto);

    MessageDto res = messageService.create(req, List.of());

    assertThat(res).isEqualTo(messageDto);
    verify(messageRepository, times(1)).save(any(Message.class));
    verify(userRepository, times(1)).findById(any());
    verify(channelRepository, times(1)).findById(any());
    verify(messageMapper, times(1)).toDto(any(Message.class));
  }

  @Test
  @DisplayName("create - 첨부 있음")
  void create_withAttachments() {
    var req = new MessageCreateRequest(channel.getId(), author.getId(), "hello");
    var attachments = List.of(
        new BinaryContentCreateRequest("a.txt", "dummy".getBytes(), "text/plain")
    );

    Message savedWithFile = new Message("hello", channel, author, List.of(file));
    ReflectionTestUtils.setField(savedWithFile, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(savedWithFile, "createdAt", message.getCreatedAt());
    ReflectionTestUtils.setField(savedWithFile, "updatedAt", message.getUpdatedAt());

    var dtoWithFile = new MessageDto(
        savedWithFile.getId(),
        savedWithFile.getCreatedAt(),
        savedWithFile.getUpdatedAt(),
        savedWithFile.getContent(),
        channel.getId(),
        new UserDto(author.getId(), author.getUsername(), author.getEmail(), null, false),
        List.of(fileDto)
    );

    given(userRepository.findById(author.getId())).willReturn(Optional.of(author));
    given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(file);
    given(binaryContentStorage.put(eq(file.getId()), any())).willReturn(file.getId());
    given(messageRepository.save(any(Message.class))).willReturn(savedWithFile);
    given(messageMapper.toDto(savedWithFile)).willReturn(dtoWithFile);

    MessageDto res = messageService.create(req, attachments);

    assertThat(res).isEqualTo(dtoWithFile);
    verify(binaryContentRepository, times(1)).save(any(BinaryContent.class));
    verify(binaryContentStorage, times(1)).put(eq(file.getId()), any());
  }

  @Test
  @DisplayName("create - 사용자 없음 -> UserNotFoundException")
  void create_userNotFound() {
    var req = new MessageCreateRequest(channel.getId(), author.getId(), "hello");
    given(userRepository.findById(author.getId())).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(req, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("create - 채널 없음 -> ChannelNotFoundException")
  void create_channelNotFound() {
    var req = new MessageCreateRequest(channel.getId(), author.getId(), "hello");
    given(userRepository.findById(author.getId())).willReturn(Optional.of(author));
    given(channelRepository.findById(channel.getId())).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(req, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // ---------- update ----------

  @Test
  @DisplayName("update - 성공")
  void update() {
    var req = new MessageUpdateRequest("updated");
    var updatedDto = new MessageDto(
        message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
        "updated", channel.getId(),
        new UserDto(author.getId(), author.getUsername(), author.getEmail(), null, false),
        List.of()
    );

    given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(updatedDto);

    MessageDto res = messageService.update(message.getId(), req);

    assertThat(res).isEqualTo(updatedDto);
    verify(messageRepository, times(1)).findById(any());
    verify(messageRepository, times(1)).save(any(Message.class));
    verify(messageMapper, times(1)).toDto(any(Message.class));
  }

  @Test
  @DisplayName("update - 메시지 없음 -> MessageNotFoundException")
  void update_notFound() {
    given(messageRepository.findById(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> messageService.update(UUID.randomUUID(), new MessageUpdateRequest("x")))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ---------- delete ----------

  @Test
  @DisplayName("delete - 첨부 없음")
  void delete_withoutAttachments() {
    given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));
    messageService.delete(message.getId());

    verify(messageRepository, times(1)).deleteById(eq(message.getId()));
  }

  @Test
  @DisplayName("delete - 첨부 있음")
  void delete_withAttachments() {
    Message withFile = new Message("hello", channel, author, List.of(file));
    ReflectionTestUtils.setField(withFile, "id", UUID.randomUUID());
    given(messageRepository.findById(withFile.getId())).willReturn(Optional.of(withFile));

    messageService.delete(withFile.getId());

    verify(binaryContentRepository, times(1)).deleteById(eq(file.getId()));
    verify(messageRepository, times(1)).deleteById(eq(withFile.getId()));
  }

  @Test
  @DisplayName("delete - 메시지 없음 -> MessageNotFoundException")
  void delete_notFound() {
    given(messageRepository.findById(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> messageService.delete(UUID.randomUUID()))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ---------- findAllByChannelId (첫 페이지) ----------

  @Test
  @DisplayName("findAllByChannelId - 첫 페이지(커서 null)")
  void findAllByChannelId_firstPage() {
    UUID chId = channel.getId();
    given(channelRepository.existsById(eq(chId))).willReturn(true);

    Slice<Message> slice = new SliceImpl<>(List.of(message), pageable, true);
    // 서비스가 첫 페이지 시작 createdAt 을 얻기 위해 사용하는 메서드
    given(messageRepository.findLastMessageAtByChannelId(eq(chId)))
        .willReturn(Optional.of(Instant.now()));
    given(messageRepository.findAllByChannelIdWithAuthor(eq(chId), any(Instant.class), eq(pageable)))
        .willReturn(slice);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    @SuppressWarnings("unchecked")
    PageResponse<MessageDto> page =
        (PageResponse<MessageDto>) Mockito.mock(PageResponse.class);
    given(pageResponseMapper.fromSlice(any(org.springframework.data.domain.Slice.class), any()))
        .willReturn(page);

    PageResponse<MessageDto> res = messageService.findAllByChannelId(chId, null, pageable);

    assertThat(res).isEqualTo(page);
    verify(messageRepository, times(1)).findAllByChannelIdWithAuthor(eq(chId), any(Instant.class), eq(pageable));
    verify(pageResponseMapper, times(1)).fromSlice(any(org.springframework.data.domain.Slice.class), any());
  }
}
