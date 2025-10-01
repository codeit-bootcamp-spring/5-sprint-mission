package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  void create_success_withoutAttachments() {
    var channelId = UUID.randomUUID();
    var authorId = UUID.randomUUID();
    var req = new MessageCreateRequest("x", UUID.randomUUID(), UUID.randomUUID());

    var channel = mock(Channel.class);
    var author = mock(User.class);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));

    var saved = mock(Message.class);
    var id = UUID.randomUUID();
    given(saved.getId()).willReturn(id);
    given(messageRepository.save(any(Message.class))).willReturn(saved);

    var dto = new MessageDto(id, Instant.now(), null, "hello", channelId,
        new UserDto(authorId, "neo", "neo@matrix.io", null, true), List.of());
    given(messageMapper.toDto(saved)).willReturn(dto);

    var result = messageService.create(req, List.of());

    assertThat(result.id()).isEqualTo(id);
    assertThat(result.content()).isEqualTo("hello");
    then(binaryContentRepository).shouldHaveNoInteractions();
    then(binaryContentStorage).shouldHaveNoInteractions();
  }

  @Test
  void create_success_withAttachments() {
    var channelId = UUID.randomUUID();
    var authorId = UUID.randomUUID();
    var req = new MessageCreateRequest("x", UUID.randomUUID(), UUID.randomUUID());

    var channel = mock(Channel.class);
    var author = mock(User.class);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));

    byte[] a = "A".getBytes();
    byte[] b = "B".getBytes();
    var attaches = List.of(new BinaryContentCreateRequest("a.txt", "text/plain", a),
        new BinaryContentCreateRequest("b.txt", "text/plain", b));

    given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(
        inv -> inv.getArgument(0));
    willDoNothing().given(binaryContentStorage).put(any(), any());

    var saved = mock(Message.class);
    var id = UUID.randomUUID();
    given(saved.getId()).willReturn(id);
    given(messageRepository.save(any(Message.class))).willReturn(saved);

    var dto = new MessageDto(id, Instant.now(), null, "file msg", channelId,
        new UserDto(authorId, "neo", "neo@matrix.io", null, true),
        List.of(new BinaryContentDto(UUID.randomUUID(), "a.txt", (long) a.length, "text/plain"),
            new BinaryContentDto(UUID.randomUUID(), "a.txt", (long) b.length, "text/plain")));
    given(messageMapper.toDto(saved)).willReturn(dto);

    var result = messageService.create(req, attaches);

    assertThat(result.attachments()).hasSize(2);
    then(binaryContentRepository).should(times(2)).save(any(BinaryContent.class));
    then(binaryContentStorage).should(times(2)).put(any(), any());
  }

  @Test
  void create_fail_channelNotFound() {
    var req = new MessageCreateRequest("x", UUID.randomUUID(), UUID.randomUUID());

    given(channelRepository.findById(req.channelId())).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(req, List.of())).isInstanceOf(
        ChannelNotFoundException.class);
  }

  @Test
  void create_fail_userNotFound() {
    var channelId = UUID.randomUUID();
    var req = new MessageCreateRequest("x", UUID.randomUUID(), UUID.randomUUID());

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mock(Channel.class)));
    given(userRepository.findById(req.authorId())).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(req, List.of())).isInstanceOf(
        UserNotFoundException.class);
  }

  @Test
  void find_success() {
    var id = UUID.randomUUID();
    var entity = mock(Message.class);
    given(messageRepository.findById(id)).willReturn(Optional.of(entity));

    var dto = new MessageDto(id, Instant.now(), null, "c", UUID.randomUUID(),
        new UserDto(UUID.randomUUID(), "u", "u@x.io", null, true), List.of());
    given(messageMapper.toDto(entity)).willReturn(dto);

    var result = messageService.find(id);

    assertThat(result).isEqualTo(dto);
  }

  @Test
  void find_fail_notFound() {
    var id = UUID.randomUUID();
    given(messageRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.find(id)).isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  void findAllByChannelId_success_sliceHasNextCursor() {
    var chId = UUID.randomUUID();
    var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

    var m1 = mock(Message.class);
    var m2 = mock(Message.class);
    var sliceMsg = new SliceImpl<>(List.of(m1, m2), pageable, false);
    given(messageRepository.findAllByChannelIdWithAuthor(eq(chId), any(Instant.class),
        eq(pageable))).willReturn(sliceMsg);

    var d1 = new MessageDto(UUID.randomUUID(), Instant.parse("2025-01-01T00:00:00Z"), null, "c1",
        chId, new UserDto(UUID.randomUUID(), "u1", "u1@x.io", null, true), List.of());
    var d2 = new MessageDto(UUID.randomUUID(), Instant.parse("2025-01-02T00:00:00Z"), null, "c2",
        chId, new UserDto(UUID.randomUUID(), "u2", "u2@x.io", null, false), List.of());
    given(messageMapper.toDto(m1)).willReturn(d1);
    given(messageMapper.toDto(m2)).willReturn(d2);

    PageResponse<com.sprint.mission.discodeit.dto.data.MessageDto> expected = PageResponse.of(
        List.of(), null, 20, false, null);
    given(pageResponseMapper.fromSlice(any(Slice.class), eq(d2.createdAt()))).willReturn(expected);

    var result = messageService.findAllByChannelId(chId, null, pageable);

    assertThat(result).isEqualTo(expected);
    then(pageResponseMapper).should().fromSlice(any(Slice.class), eq(d2.createdAt()));
  }

  @Test
  void findAllByChannelId_empty_slice() {
    UUID chId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(1, 20);

    Slice<Message> sliceMsg = new SliceImpl<>(List.of(), pageable, false);
    given(messageRepository.findAllByChannelIdWithAuthor(eq(chId), any(Instant.class),
        eq(pageable))).willReturn(sliceMsg);

    PageResponse<com.sprint.mission.discodeit.dto.data.MessageDto> expected = PageResponse.of(
        List.of(), null, 20, false, null);

    given(pageResponseMapper.fromSlice(any(Slice.class), isNull())).willReturn(expected);

    var result = messageService.findAllByChannelId(chId, null, pageable);

    assertThat(result).isEqualTo(expected);
    then(pageResponseMapper).should().fromSlice(any(Slice.class), isNull());
  }


  @Test
  void update_success() {
    var id = UUID.randomUUID();
    var req = new MessageUpdateRequest("new content");

    var entity = mock(Message.class);
    given(messageRepository.findById(id)).willReturn(Optional.of(entity));

    var dto = new MessageDto(id, Instant.now(), Instant.now(), "new content", UUID.randomUUID(),
        new UserDto(UUID.randomUUID(), "u", "u@x.io", null, true), List.of());
    given(messageMapper.toDto(entity)).willReturn(dto);

    var result = messageService.update(id, req);

    then(entity).should().update(eq("new content"));
    assertThat(result.content()).isEqualTo("new content");
  }

  @Test
  void update_fail_notFound() {
    var id = UUID.randomUUID();
    var req = new MessageUpdateRequest("x");
    given(messageRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(id, req)).isInstanceOf(
        NoSuchElementException.class);
  }

  @Test
  void delete_success() {
    var id = UUID.randomUUID();
    given(messageRepository.existsById(id)).willReturn(true);

    messageService.delete(id);

    then(messageRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_notFound() {
    var id = UUID.randomUUID();
    given(messageRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> messageService.delete(id)).isInstanceOf(
        MessageNotFoundException.class);
  }
}