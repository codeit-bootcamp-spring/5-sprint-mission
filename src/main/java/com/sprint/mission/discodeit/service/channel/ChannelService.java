package com.sprint.mission.discodeit.service.channel;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStrip;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelSaveResponse;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;

  public List<ChannelResponse> findAll(UUID userId) {
    User u = userRepository.getOrThrow(userId);
    List<Channel> channels = channelRepository.findAllById(u.getChannelIds());

    Set<UUID> lastMessageIds = channels.stream()
        .map(Channel::getLastMessageId)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

    Map<UUID, Instant> lastMessageAtMap = messageRepository.findAllCreatedAtById(lastMessageIds);

    return channels.stream()
        .map(c -> {
          Instant lastMessageAt = c.getLastMessageId()
              .map(lastMessageAtMap::get)
              .orElse(null);
          return ChannelResponse.from(c, lastMessageAt);
        })
        .toList();
  }

  @Transactional
  public ChannelSaveResponse create(PublicChannelCreateRequest req) {
    String name = nullOrStrip(req.name());
    String description = nullOrStrip(req.description());
    return ChannelSaveResponse.from(channelRepository.save(new Channel(name, description)));
  }

  @Transactional
  public ChannelSaveResponse create(PrivateChannelCreateRequest req) {
    Set<UUID> ids = req.participantIds();
    List<User> users = userRepository.findAllById(ids);

    if (users.size() != ids.size()) {
      Set<UUID> found = users.stream().map(User::getId).collect(Collectors.toSet());
      UUID missing = ids.stream().filter(id -> !found.contains(id)).findFirst().orElse(null);
      throw new NotFoundException("User with id %s not found".formatted(missing));
    }

    if (users.size() == 2) {
      UUID userId1 = users.get(0).getId();
      UUID userId2 = users.get(1).getId();
      if (channelRepository.existsBetween(userId1, userId2)) {
        throw new DuplicateResourceException(
            "Private channel between %s, %s already exists".formatted(userId1, userId2));
      }
    }

    Channel c = channelRepository.save(new Channel(ids));

    users.forEach(u -> u.joinChannel(c.getId()));
    userRepository.saveAll(users);

    return ChannelSaveResponse.from(c);
  }

  @Transactional
  public void delete(UUID channelId) {
    if (!channelRepository.softDeleteById(channelId)) {
      throw new NotFoundException("Channel with id %s not found".formatted(channelId));
    }
  }

  @Transactional
  public ChannelSaveResponse update(UUID channelId, PublicChannelUpdateRequest req) {
    Channel c = channelRepository.getOrThrow(channelId);

    if (c.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    if (req.newName() == null && req.newDescription() == null) {
      return ChannelSaveResponse.from(c);
    }

    String name = c.getName();
    if (req.newName() != null) {
      String newName = req.newName().strip();
      if (!newName.isBlank()) {
        name = newName;
      }
    }

    String description = c.getDescription();
    if (req.newDescription() != null) {
      description = req.newDescription().strip();
    }

    boolean nameChanged = !Objects.equals(c.getName(), name);
    boolean descChanged = !Objects.equals(c.getDescription(), description);

    if (!nameChanged && !descChanged) {
      return ChannelSaveResponse.from(c);
    }

    if (nameChanged) {
      c.changeName(name);
    }
    if (descChanged) {
      c.changeDescription(description);
    }

    return ChannelSaveResponse.from(channelRepository.save(c));
  }
}
