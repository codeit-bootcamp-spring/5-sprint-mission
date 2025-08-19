package com.sprint.mission.discodeit.service.channel;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.dto.request.chnanel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.support.StringUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  @Transactional
  public ChannelResponse create(PublicChannelCreateRequest req) {
    String name = StringUtil.stripOrNull(req.name());
    String description = StringUtil.stripOrNull(req.description());
    return ChannelResponse.from(channelRepository.save(new Channel(name, description)));
  }

  @Transactional
  public ChannelResponse create(PrivateChannelCreateRequest req) {
    for (UUID id : req.participantIds()) {
      userRepository.getOrThrow(id);
    }

    return ChannelResponse.from(channelRepository.save(new Channel(req.participantIds())));
  }

  @Transactional
  public void delete(UUID channelId) {
    if (!channelRepository.softDeleteById(channelId)) {
      throw new NotFoundException("채널을(를) 찾을 수 없습니다: " + channelId);
    }
  }

  @Transactional
  public ChannelResponse update(UUID channelId, PublicChannelUpdateRequest req) {
    Channel c = channelRepository.getOrThrow(channelId);

    if (ChannelType.PRIVATE.equals(c.getType())) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    String newName = StringUtil.stripOrNull(req.newName());
    String newDescription = StringUtil.stripOrNull(req.newDescription());
    if (c.getName().equals(newName) && c.getDescription().equals(newDescription)) {
      return ChannelResponse.from(c);
    }

    c.changeName(newName);
    c.changeDescription(newDescription);

    return ChannelResponse.from(channelRepository.save(c));
  }
}
