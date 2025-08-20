package com.sprint.mission.discodeit.service.channel;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelSaveResponse;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.support.StringUtil;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    Set<UUID> ids = channels.stream().map(Channel::getId).collect(Collectors.toSet());
    Map<UUID, Instant> lastMap = messageRepository.findLastMessageAtByChannelIds(ids);
    return channels.stream()
        .map(c -> ChannelResponse.from(c, lastMap.get(c.getId())))
        .toList();
  }

  @Transactional
  public ChannelSaveResponse create(PublicChannelCreateRequest req) {
    String name = StringUtil.nullOrStrip(req.name());
    String description = StringUtil.nullOrStrip(req.description());
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

    String targetName = c.getName();
    if (req.newName() != null) {
      String stripped = req.newName().strip();
      if (!stripped.isBlank()) {
        targetName = stripped;
      }
    }

    String targetDescription = c.getDescription();
    if (req.newDescription() != null) {
      targetDescription = req.newDescription().strip();
    }

    boolean nameChanged = !Objects.equals(c.getName(), targetName);
    boolean descChanged = !Objects.equals(c.getDescription(), targetDescription);

    if (!nameChanged && !descChanged) {
      return ChannelSaveResponse.from(c);
    }

    if (nameChanged) {
      c.changeName(targetName);
    }
    if (descChanged) {
      c.changeDescription(targetDescription);
    }

    return ChannelSaveResponse.from(channelRepository.save(c));
  }
}
