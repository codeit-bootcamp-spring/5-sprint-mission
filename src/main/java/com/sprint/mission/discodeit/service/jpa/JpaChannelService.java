package com.sprint.mission.discodeit.service.jpa;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JpaChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    return toDto(channelRepository.save(channel));
  }

  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel saved = channelRepository.save(channel);

    request.participantIds().forEach(userId -> {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
      readStatusRepository.save(new ReadStatus(user, saved, saved.getCreatedAt()));
    });

    List<UserDto> participants = request.participantIds().stream()
        .map(userId -> {
          User user = userRepository.findById(userId)
              .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
          return userMapper.toDto(user, user.isOnline());
        })
        .toList();

    return new ChannelDto(
        saved.getId(),
        saved.getType(),
        saved.getName(),
        saved.getDescription(),
        participants
    );
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> myChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(rs -> rs.getChannel().getId())
        .toList();

    return channelRepository.findAll().stream()
        .filter(ch -> ch.getType() == ChannelType.PUBLIC || myChannelIds.contains(ch.getId()))
        .map(this::toDto)
        .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    channel.update(request.newName(), request.newDescription());
    return toDto(channel); // Dirty Checking으로 자동 반영
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());
    channelRepository.delete(channel);
  }

  private ChannelDto toDto(Channel channel) {
    List<UserDto> participants = channel.getType() == ChannelType.PRIVATE
        ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .map(rs -> {
          User user = rs.getUser();
          return userMapper.toDto(user, user.isOnline());
        })
        .toList()
        : Collections.emptyList();

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants
    );
  }
}
