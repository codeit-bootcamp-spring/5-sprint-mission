package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.ChannelDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ChannelDto.Detail;
import com.sprint.mission.discodeit.dto.ChannelDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  private UUID userId;
  private Channel channel;
  private Detail detail;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    channel = Channel.builder()
                     .name("Test Channel")
                     .type(ChannelType.PUBLIC)
                     .description("Test Description")
                     .build();

    detail = Detail.builder()
                   .id(UUID.randomUUID())
                   .name(channel.getName())
                   .type(channel.getType())
                   .description(channel.getDescription())
                   .build();
  }

  @Test
  void createChannel_success() {
    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    doReturn(detail).when(channelMapper)
                    .toDetail(any(Channel.class));

    Detail result = channelService.create(CreateCommand.builder()
                                                       .name(channel.getName())
                                                       .type(channel.getType())
                                                       .description(channel.getDescription())
                                                       .build());

    then(channelRepository).should()
                           .save(any(Channel.class));
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(channel.getName());
  }

  @Test
  void updateChannel_success() {

    given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
    doReturn(detail).when(channelMapper)
                    .toDetail(any(Channel.class));

    UpdateCommand command = UpdateCommand.builder()
                                         .id(channel.getId())
                                         .name("Updated Name")
                                         .description("Updated Description")
                                         .build();

    Detail result = channelService.update(command);

    then(channelRepository).should()
                           .findById(channel.getId());

    assertThat(channel.getName()).isEqualTo("Updated Name");
    assertThat(channel.getDescription()).isEqualTo("Updated Description");
    assertThat(result).isNotNull();
  }


  @Test
  void updateChannel_notFound_fail() {
    given(channelRepository.findById(channel.getId())).willReturn(Optional.empty());

    UpdateCommand command = UpdateCommand.builder()
                                         .id(channel.getId())
                                         .name("Updated Name")
                                         .build();

    assertThrows(ChannelNotFoundException.class, () -> channelService.update(command));
  }

  @Test
  void deleteChannel_success() {
    given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));

    channelService.delete(channel.getId());

    then(channelRepository).should()
                           .delete(channel);
  }

  @Test
  void deleteChannel_notFound_fail() {
    given(channelRepository.findById(channel.getId())).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class, () -> channelService.delete(channel.getId()));
  }

  @Test
  void findAllByUserId_success() {
    given(channelRepository.findByType(ChannelType.PUBLIC)).willReturn(List.of(channel));
    given(readStatusRepository.findByUserId(userId)).willReturn(List.of());
    doReturn(detail).when(channelMapper)
                    .toDetail(any(Channel.class));

    List<Detail> result = channelService.findAllByUserId(userId);

    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)
                     .getName()).isEqualTo(channel.getName());
  }
}
