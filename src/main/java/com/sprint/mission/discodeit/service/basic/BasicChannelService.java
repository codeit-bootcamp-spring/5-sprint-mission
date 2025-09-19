package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
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
    Channel channel = channelRepository.save(new Channel(
        ChannelType.PUBLIC,
        request.name(),
        request.description()));

    return channelMapper.toDto(
        channel,
        findParticipants(channel.getId()),
        findLastMessageAt(channel.getId()));
  }

  @Override
  @Transactional
  public ChannelDto create(@Valid PrivateChannelCreateRequest request) {

    List<User> participants = request.participantIds()
        .stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withDetail("userId", userId)))
        .toList();

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    for (User user : participants) {
      readStatusRepository.save(
          new ReadStatus(channel.getCreatedAt(), user, channel));
    }

    return channelMapper.toDto(
        channel,
        findParticipants(channel.getId()),
        findLastMessageAt(channel.getId()));
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto findById(UUID channelId) {
    Channel channel = validateId(channelId);

    return channelMapper.toDto(channel, findParticipants(channelId), findLastMessageAt(channelId));
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
        .map(channel -> channelMapper.toDto(
            channel,
            findParticipants(channel.getId()),
            findLastMessageAt(channel.getId())))
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId,
      @Valid PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel channel = validateId(channelId);

    if (channel.getType() == ChannelType.PRIVATE) {
      throw PrivateChannelUpdateException.withDetail("channelId", channelId);
    }
    channel.update(publicChannelUpdateRequest.newName(),
        publicChannelUpdateRequest.newDescription());
    channelRepository.save(channel);

    return channelMapper.toDto(
        channel,
        findParticipants(channelId),
        findLastMessageAt(channelId));
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    validateId(channelId);

    readStatusRepository.findAllByChannelId(channelId)
        .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));
    messageRepository.findAll().stream()
        .filter(message -> message.getChannel().getId().equals(channelId))
        .forEach(message -> messageRepository.deleteById(message.getId()));
    channelRepository.deleteById(channelId);
  }

  private List<User> findParticipants(UUID channelId) {
    return userRepository.findAllByIdIn(
        readStatusRepository.findAllByChannelId(channelId).stream()
            .map(readStatus -> readStatus.getUser().getId())
            .toList()
    );
  }

  private Instant findLastMessageAt(UUID channelId) {
    return messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(channelId)
        .map(Message::getCreatedAt)
        .orElse(null);
  }

  private Channel validateId(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withDetail("channelId", channelId));
  }
}
