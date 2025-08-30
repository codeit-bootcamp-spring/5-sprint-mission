package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service("channelService")
@RequiredArgsConstructor
@Validated
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Override
  public Channel create(@Valid PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(),
        request.description());
    return channelRepository.save(channel);
  }

  @Override
  public Channel create(@Valid PrivateChannelCreateRequest request) {

    List<User> participants = request.participantIds()
        .stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found [" + userId + "]"))
        )
        .toList();

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    for (User user : participants) {
      readStatusRepository.save(
          new ReadStatus(channel.getCreatedAt(), user, channel));
    }

    return channelRepository.save(channel);
  }

  public ChannelFindResponse findById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("findById : 채널을 찾을 수 없습니다. [" + channelId + "]"));

    return findById(channel);
  }

  @Override
  public ChannelFindResponse findById(Channel channel) {
    UUID channelId = channel.getId();

    Instant lastMessageAt = messageRepository.findAll().stream()
        .filter(m -> m.getChannel().getId().equals(channelId))
        .max(Comparator.comparingLong(m -> m.getCreatedAt().getEpochSecond()))
        .map(Message::getCreatedAt)
        .orElse(null);

    return ChannelFindResponse.builder()
        .id(channelId)
        .name(channel.getName())
        .participantIds(channel.getType() != ChannelType.PRIVATE ?
            null : readStatusRepository.findByChannelId(channelId).stream()
            .map(readStatus -> readStatus.getUser().getId())
            .toList())
        .description(channel.getDescription())
        .type(channel.getType())
        .lastMessageAt(lastMessageAt)
        .build();
  }

  @Override
  public List<ChannelFindResponse> findAllByUserId(UUID userId) {
    Map<UUID, ReadStatus> readStatusMap = readStatusRepository.findByUserId(userId).stream()
        .collect(
            Collectors.toMap(readStatus -> readStatus.getUser().getId(), readStatus -> readStatus));

    return channelRepository.findAll().stream()
        .map(channel -> {
          if (channel.getType() == ChannelType.PUBLIC) {
            return findById(channel);
          }
          if (readStatusMap.containsKey(channel.getId())) {
            return findById(channel);
          }
          return null;
        })
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public Channel update(UUID channelId,
      @Valid PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("update : 채널을 찾을 수 없습니다. [" + channelId + "]"));
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("update : Private 채널은 업데이트 할 수 없습니다. [" + channelId + "]");
    }
    channel.update(publicChannelUpdateRequest.newName(),
        publicChannelUpdateRequest.newDescription());
    return channelRepository.save(channel);
  }

  @Override
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("delete : 채널을 찾을 수 없습니다. [" + channelId + "]");
    }

    readStatusRepository.findByChannelId(channelId)
        .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));
    messageRepository.findAll().stream()
        .filter(message -> message.getChannel().getId().equals(channelId))
        .forEach(message -> messageRepository.deleteById(message.getId()));
    channelRepository.deleteById(channelId);
  }
}
