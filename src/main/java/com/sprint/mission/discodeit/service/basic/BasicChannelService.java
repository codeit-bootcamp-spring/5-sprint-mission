package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service("channelService")
@RequiredArgsConstructor
@Validated
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  public ChannelDto create(@Valid PublicChannelCreateRequest request) {
    return channelMapper.toDto(channelRepository.save(new Channel(
        ChannelType.PUBLIC,
        request.name(),
        request.description())));
  }

  @Override
  @Transactional
  public ChannelDto create(@Valid PrivateChannelCreateRequest request) {

    List<User> participants = request.participantIds()
        .stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found [" + userId + "]"))
        )
        .toList();

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    for (User user : participants) {
      readStatusRepository.save(
          new ReadStatus(channel.getCreatedAt(), user, channel));
    }

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto findById(UUID channelId) {
    return channelMapper.toDto(channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found [" + channelId + "]")));
  }


  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel -> (
            readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList()
                .contains(channel.getId()) && channel.getType().equals(ChannelType.PRIVATE))
            || channel.getType().equals(ChannelType.PUBLIC))
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId,
      @Valid PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("update : 채널을 찾을 수 없습니다. [" + channelId + "]"));
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("update : Private 채널은 업데이트 할 수 없습니다. [" + channelId + "]");
    }
    channel.update(publicChannelUpdateRequest.newName(),
        publicChannelUpdateRequest.newDescription());

    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("delete : 채널을 찾을 수 없습니다. [" + channelId + "]");
    }

    readStatusRepository.findAllByChannelId(channelId)
        .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));
    messageRepository.findAll().stream()
        .filter(message -> message.getChannel().getId().equals(channelId))
        .forEach(message -> messageRepository.deleteById(message.getId()));
    channelRepository.deleteById(channelId);
  }
}
