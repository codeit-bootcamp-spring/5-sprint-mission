package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  void createPublic_success() {
    var req = new PublicChannelCreateRequest("general", "desc");

    var saved = mock(Channel.class);
    var id = UUID.randomUUID();
    given(saved.getId()).willReturn(id);
    given(saved.getType()).willReturn(ChannelType.PUBLIC);
    given(saved.getName()).willReturn("general");

    given(channelRepository.save(any(Channel.class))).willReturn(saved);

    var dto = new ChannelDto(id, ChannelType.PUBLIC, "general", "desc", List.of(), null);
    given(channelMapper.toDto(saved)).willReturn(dto);

    var result = channelService.create(req);

    assertThat(result.id()).isEqualTo(id);
    assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(result.name()).isEqualTo("general");
    then(channelRepository).should().save(any(Channel.class));
  }

  @Test
  void createPublic_fail_invalidName() {
    var req = new PublicChannelCreateRequest("", "desc");
    assertThatThrownBy(() -> channelService.create(req)).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void createPrivate_success() {
    var u1 = UUID.randomUUID();
    var u2 = UUID.randomUUID();
    var req = new PrivateChannelCreateRequest(List.of(u1, u2));

    var channel = mock(Channel.class);
    var chId = UUID.randomUUID();
    given(channel.getId()).willReturn(chId);
    given(channel.getType()).willReturn(ChannelType.PRIVATE);
    given(channel.getCreatedAt()).willReturn(Instant.now());
    given(channelRepository.save(any(Channel.class))).willReturn(channel);

    var user1 = mock(User.class);
    var user2 = mock(User.class);
    given(userRepository.findAllById(eq(List.of(u1, u2)))).willReturn(List.of(user1, user2));

    given(readStatusRepository.saveAll(any())).willAnswer(inv -> inv.getArgument(0));

    var dto = new ChannelDto(chId, ChannelType.PRIVATE, null, null,
        List.of(new UserDto(u1, "u1", "u1@x.io", null, true),
            new UserDto(u2, "u2", "u2@x.io", null, false)), null);
    given(channelMapper.toDto(channel)).willReturn(dto);

    var result = channelService.create(req);

    assertThat(result.id()).isEqualTo(chId);
    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
    then(userRepository).should().findAllById(eq(List.of(u1, u2)));

    @SuppressWarnings("unchecked") ArgumentCaptor<Iterable<ReadStatus>> captor = ArgumentCaptor.forClass(
        Iterable.class);
    then(readStatusRepository).should().saveAll(captor.capture());
    int count = 0;
    for (ReadStatus ignored : captor.getValue()) {
      count++;
    }
    assertThat(count).isEqualTo(2);
  }


  @Test
  void createPrivate_fail_participantsTooFew() {
    var req = new PrivateChannelCreateRequest(List.of(UUID.randomUUID()));
    assertThatThrownBy(() -> channelService.create(req)).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void find_success() {
    var ch = mock(Channel.class);
    var id = UUID.randomUUID();
    given(channelRepository.findById(id)).willReturn(Optional.of(ch));

    var dto = new ChannelDto(id, ChannelType.PUBLIC, "general", "d", List.of(), null);
    given(channelMapper.toDto(ch)).willReturn(dto);

    var result = channelService.find(id);

    assertThat(result).isEqualTo(dto);
  }

  @Test
  void find_fail_notFound() {
    var id = UUID.randomUUID();
    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.find(id)).isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  void findAllByUserId_success_includesPublic_andMyPrivate() {
    var userId = UUID.randomUUID();

    var ch1 = mock(Channel.class);
    var ch1Id = UUID.randomUUID();
    given(ch1.getId()).willReturn(ch1Id);
    var rs1 = mock(ReadStatus.class);
    given(rs1.getChannel()).willReturn(ch1);

    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(rs1));

    var publicCh = mock(Channel.class);
    var publicId = UUID.randomUUID();
    given(publicCh.getId()).willReturn(publicId);
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),
        eq(List.of(ch1Id)))).willReturn(List.of(publicCh, ch1));

    var dtoPublic = new ChannelDto(publicId, ChannelType.PUBLIC, "general", null, List.of(), null);
    var dtoPrivate = new ChannelDto(ch1Id, ChannelType.PRIVATE, null, null, List.of(), null);
    given(channelMapper.toDto(publicCh)).willReturn(dtoPublic);
    given(channelMapper.toDto(ch1)).willReturn(dtoPrivate);

    var result = channelService.findAllByUserId(userId);

    assertThat(result).containsExactly(dtoPublic, dtoPrivate);
    then(readStatusRepository).should().findAllByUserId(userId);
    then(channelRepository).should()
        .findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), eq(List.of(ch1Id)));
  }

  @Test
  void findAllByUserId_empty_whenNoChannels() {
    var userId = UUID.randomUUID();
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of())).willReturn(
        List.of());

    var result = channelService.findAllByUserId(userId);

    assertThat(result).isEmpty();
  }

  @Test
  void update_success_publicChannel() {
    var id = UUID.randomUUID();
    var req = new PublicChannelUpdateRequest("newName", "newDesc");

    var ch = mock(Channel.class);
    given(channelRepository.findById(id)).willReturn(Optional.of(ch));
    given(ch.getType()).willReturn(ChannelType.PUBLIC);

    var dto = new ChannelDto(id, ChannelType.PUBLIC, "newName", "newDesc", List.of(), null);
    given(channelMapper.toDto(ch)).willReturn(dto);

    var result = channelService.update(id, req);

    then(ch).should().update(eq("newName"), eq("newDesc"));
    assertThat(result.name()).isEqualTo("newName");
  }

  @Test
  void update_fail_privateChannel() {
    var id = UUID.randomUUID();
    var req = new PublicChannelUpdateRequest("newName", "newDesc");

    var ch = mock(Channel.class);
    given(channelRepository.findById(id)).willReturn(Optional.of(ch));
    given(ch.getType()).willReturn(ChannelType.PRIVATE);

    assertThatThrownBy(() -> channelService.update(id, req)).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void update_fail_notFound() {
    var id = UUID.randomUUID();
    var req = new PublicChannelUpdateRequest("newName", "newDesc");

    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.update(id, req)).isInstanceOf(
        NoSuchElementException.class);
  }

  @Test
  void delete_success() {
    var id = UUID.randomUUID();
    given(channelRepository.existsById(id)).willReturn(true);

    channelService.delete(id);

    then(messageRepository).should().deleteAllByChannelId(id);
    then(readStatusRepository).should().deleteAllByChannelId(id);
    then(channelRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_notFound() {
    var id = UUID.randomUUID();
    given(channelRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> channelService.delete(id)).isInstanceOf(
        ChannelNotFoundException.class);
  }
}
